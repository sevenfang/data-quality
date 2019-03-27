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
package org.talend.dataquality.datamasking.functions.misc;

import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class SetToNullTest {

    private Object output;

    private SetToNull<?> stn = new SetToNull<>();

    @Test
    public void returnsNull() {
        output = stn.generateMaskedRow(null);
        assertNull(output);
    }

}
