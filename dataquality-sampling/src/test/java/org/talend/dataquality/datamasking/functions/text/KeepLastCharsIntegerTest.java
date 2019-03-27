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
package org.talend.dataquality.datamasking.functions.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class KeepLastCharsIntegerTest {

    private int output;

    private int input = 123456;

    private KeepLastCharsInteger klag = new KeepLastCharsInteger();

    @Before
    public void setUp() {
        klag.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        klag.parse("3", false);
        output = klag.generateMaskedRow(input);
        assertEquals(38456, output); // $NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        klag.parse("7", false);
        output = klag.generateMaskedRow(input);
        assertEquals(input, output);
    }

    @Test
    public void negativeParameter() {
        try {
            klag.parse("-2", false);
            fail("should get exception with input " + Arrays.toString(klag.getParsedParameters())); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = klag.generateMaskedRow(input);
        assertEquals(0, output);
    }

}
