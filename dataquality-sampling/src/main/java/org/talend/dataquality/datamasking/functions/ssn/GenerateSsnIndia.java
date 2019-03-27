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
import org.talend.dataquality.datamasking.utils.ssn.UtilsSsnIndia;

/**
 * /**
 *
 * @author dprot
 * <p>
 * Indian pattern: abbbbbbbbbbc
 * a: 1 -> 9
 * b: 0 -> 9
 * c: checksum with Verhoeff' algorithm
 */
public class GenerateSsnIndia extends FunctionString {

    private static final long serialVersionUID = -8621894245597689328L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder();
        result.append(1 + r.nextInt(9));
        for (int i = 0; i < 10; i++) {
            result.append(nextRandomDigit(r));
        }

        // Add the security key specified for Indian SSN
        result.append(UtilsSsnIndia.computeIndianKey(result));

        return result.toString();
    }
}
