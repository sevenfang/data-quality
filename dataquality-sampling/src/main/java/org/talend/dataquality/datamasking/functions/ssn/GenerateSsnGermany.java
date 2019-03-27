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
package org.talend.dataquality.datamasking.functions.ssn;

import java.util.Random;

import org.talend.dataquality.datamasking.functions.FunctionString;

/**
 * The Germany ssn has 11 numbers. As we generate every number from 0 to 8 randomly, it can generate 31 381 059 609 (9
 * power 11) ssn numbers.<br>
 * However, every generation is independent, this class cannot guarantee the difference among all the execution.<br>
 */
public class GenerateSsnGermany extends FunctionString {

    private static final long serialVersionUID = -3060510098713442546L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 11; ++i) {
            result.append(nextRandomDigit(r));
        }
        return result.toString();
    }

}
