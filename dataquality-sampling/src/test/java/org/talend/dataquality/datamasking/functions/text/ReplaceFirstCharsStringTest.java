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
import org.talend.dataquality.datamasking.FunctionMode;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsStringTest {

    private String output;

    private String input = "123456";

    private ReplaceFirstCharsString rfcs = new ReplaceFirstCharsString();

    @Before
    public void setUp() throws Exception {
        rfcs.setRandom(new Random(42));
    }

    @Test
    public void defaultBehavior() {
        rfcs.parse("3,y", false);
        output = rfcs.generateMaskedRow(input);
        assertEquals("yyy456", output);
    }

    @Test
    public void random() {
        rfcs.parse("3,y", false);
        output = rfcs.generateMaskedRow(input, FunctionMode.RANDOM);
        assertEquals("yyy456", output);
    }

    @Test
    public void consistent() {
        rfcs.parse("3", false);
        output = rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void consistentNoSeed() {
        rfcs.setRandom(null);
        rfcs.parse("3", false);
        output = rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT);
        assertEquals(output, rfcs.generateMaskedRow(input, FunctionMode.CONSISTENT));
    }

    @Test
    public void emptyReturnsEmpty() {
        rfcs.setKeepEmpty(true);
        output = rfcs.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void dummyHighParameter() {
        rfcs.parse("7", false);
        output = rfcs.generateMaskedRow(input);
        assertEquals("038405", output);
    }

    @Test
    public void letterInParameters() {
        try {
            rfcs.parse("0,xs", false);
            fail("should get exception with input " + Arrays.toString(rfcs.getParsedParameters())); //$NON-NLS-1$
        } catch (Exception e) {
            assertTrue("expect illegal argument exception ", e instanceof IllegalArgumentException); //$NON-NLS-1$
        }
        output = rfcs.generateMaskedRow(input);
        assertEquals("", output); // $NON-NLS-1$
    }

}
