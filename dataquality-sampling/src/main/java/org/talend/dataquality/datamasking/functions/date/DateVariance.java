// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.functions.date;

import static org.talend.dataquality.datamasking.FunctionMode.CONSISTENT;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.CharactersOperationUtils;
import org.talend.dataquality.datamasking.semantic.AbstractDateFunction;

/**
 * created by jgonzalez on 18 juin 2015. This function will modify the input date by adding or retieving a number of
 * days lower than the parameter.
 *
 */
public class DateVariance extends AbstractDateFunction {

    private static final long serialVersionUID = 7723968828358381315L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DateVariance.class);

    private static final Long NB_MS_PER_DAY = 86400000L;

    private int integerParam = 31;

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        if (CharactersOperationUtils.validParameters1Number(parameters))
            integerParam = Integer.parseInt(parameters[0]);
        else
            LOGGER.info("The parameter is ignored because it's not a positive integer");

    }

    @Override
    protected Date doGenerateMaskedField(Date date, FunctionMode mode) {
        Random r = rnd;
        if (CONSISTENT == mode) {
            r = getRandomForObject(date);
        }
        return doGenerateMaskedField(date, r);
    }

    @Override
    protected Date doGenerateMaskedField(Date date) {
        return doGenerateMaskedField(date, rnd);
    }

    @Override
    protected Date doGenerateMaskedField(Date date, Random r) {
        if (date != null) {
            long variation;
            if (integerParam < 0) {
                integerParam *= -1;
            } else if (integerParam == 0) {
                integerParam = 31;
            }
            do {
                variation = Math.round((r.nextDouble() * 2 - 1) * integerParam * NB_MS_PER_DAY);
            } while (variation == 0);
            Long originalDate = date.getTime();
            Date newDate = new Date(originalDate + variation);
            return newDate;
        } else {
            return new Date(System.currentTimeMillis());
        }
    }
}
