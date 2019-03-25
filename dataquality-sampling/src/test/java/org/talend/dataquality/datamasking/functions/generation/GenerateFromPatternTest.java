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
package org.talend.dataquality.datamasking.functions.generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * created by jgonzalez on 19 août 2015 Detailled comment
 *
 */
public class GenerateFromPatternTest {

    private String output;

    private GenerateFromPattern gfp = new GenerateFromPattern();

    @Before
    public void setUp() throws Exception {
        gfp.setRandom(new Random(42L));
    }

    @Test
    public void standardPattern() {
        gfp.parse("aaAA99", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("ahWM05", output); //$NON-NLS-1$
    }

    @Test
    public void patternWithReference() {
        gfp.parse("aaAA99\\1, @gmail.com", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("ahWM05@gmail.com", output); //$NON-NLS-1$
    }

    @Test
    public void patternWithReferences() {
        gfp.parse("\\1aA9\\2hHkK\\3C\\4G,latin:,;japanese:,;chinese:,;korean:", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("latin:aH8;japanese:hぽﾀㇵ;chinese:睻;korean:롖", output); //$NON-NLS-1$
    }

    @Test
    public void patternWithMissingReference() {
        gfp.parse("aaAA99\\, @gmail.com", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("ahWM05\\", output); //$NON-NLS-1$
    }

    @Test
    public void patternWithWrongReference() {
        gfp.parse("aaAA99\\2, @gmail.com", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("ahWM05\\2", output); //$NON-NLS-1$
    }

    @Test
    public void wrongPattern() {
        gfp.parse("", false); //$NON-NLS-1$
        output = gfp.generateMaskedRow(null);
        assertEquals("", output);
    }

    @Test
    public void nullPattern() {
        gfp.setKeepNull(true);
        output = gfp.generateMaskedRow(null);
        assertNull(output);
    }
}
