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
package org.talend.dataquality.datamasking.functions.phone;

import java.util.Random;

import org.talend.dataquality.datamasking.functions.FunctionString;

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 */
public class GeneratePhoneNumberGermany extends FunctionString {

    private static final long serialVersionUID = -2193040590064798675L;

    @Override
    protected String doGenerateMaskedField(String str) {
        return doGenerateMaskedFieldWithRandom(str, rnd);
    }

    @Override
    protected String doGenerateMaskedFieldWithRandom(String str, Random r) {
        StringBuilder result = new StringBuilder(EMPTY_STRING);
        int choice = r.nextInt(4);
        switch (choice) {
        case 0:
            result.append("030 "); //$NON-NLS-1$
            break;
        case 1:
            result.append("040 "); //$NON-NLS-1$
            break;
        case 2:
            result.append("069 "); //$NON-NLS-1$
            break;
        case 3:
            result.append("089 "); //$NON-NLS-1$
            break;
        default:
            break;
        }
        for (int i = 0; i < 8; ++i) {
            result.append(nextRandomDigit(r));
        }
        return result.toString();
    }
}
