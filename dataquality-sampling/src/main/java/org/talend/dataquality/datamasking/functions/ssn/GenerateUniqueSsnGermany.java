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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.datamasking.generic.fields.AbstractField;
import org.talend.dataquality.datamasking.generic.fields.FieldInterval;

/**
 * @author dprot class global comment. Detailled comment
 * 
 * German pattern: aaaaaaaaaaaa aaaaaaaaaaaa: 1 -> 100000000000 - 1
 */

public class GenerateUniqueSsnGermany extends AbstractGenerateUniqueSsn {

    private static final long serialVersionUID = -2321693247791991249L;

    @Override
    protected List<AbstractField> createFieldsListFromPattern() {
        List<AbstractField> fields = new ArrayList<>();
        fields.add(new FieldInterval(BigInteger.ONE, BigInteger.valueOf(99999999999L)));

        return fields;
    }

    @Override
    protected List<String> splitFields(String str) {
        List<String> strs = new ArrayList<>();
        strs.add(str.substring(0, 11));
        return strs;
    }
}
