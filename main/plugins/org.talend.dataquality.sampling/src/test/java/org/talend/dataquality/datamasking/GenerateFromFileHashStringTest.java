// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileHashStringTest {

    private String output;

    private String path = "/home/jgonzalez/Bureau/data/name.txt"; //$NON-NLS-1$

    private GenerateFromFileHashString gffhs = new GenerateFromFileHashString();

    @Before
    public void setUp() throws Exception {
        gffhs.parse(path, false, new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gffhs.generateMaskedRow(null);
        assertEquals(output, "Brad"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gffhs.keepNull = true;
        output = gffhs.generateMaskedRow(null);
        assertEquals(output, null);
    }
}
