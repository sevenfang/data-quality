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
package org.talend.dataquality.datamasking.functions.number;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.CharactersOperationUtils;
import org.talend.dataquality.datamasking.functions.Function;

/**
 * created by jgonzalez on 19 juin 2015. This function will modify the input data by multiplying it by a number between
 * the parameter and its opposite.
 *
 */
public abstract class NumericVariance<T> extends Function<T> {

    private static final long serialVersionUID = -9042942041517353551L;

    private static final Logger LOGGER = LoggerFactory.getLogger(NumericVariance.class);

    protected int rate = 0;

    private int integerParam = 10;

    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        if (CharactersOperationUtils.validParameters1Number(parameters))
            integerParam = Integer.parseInt(parameters[0]);
        else
            LOGGER.info("The parameter is ignored because it's not a positive integer");
        if (integerParam == 0) {
            integerParam = 10;
        } else if (integerParam < 0) {
            integerParam *= -1;
        }
    }

    protected void init() {
        init(rnd);
    }

    protected void init(Random r) {
        do {
            rate = r.nextInt(2 * integerParam) - integerParam;
        } while (rate == 0);
    }

    @Override
    protected abstract T doGenerateMaskedField(T t);
}
