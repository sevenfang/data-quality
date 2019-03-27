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
package org.talend.dataquality.datamasking.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.date.DateVariance;
import org.talend.dataquality.duplicating.AllDataqualitySamplingTests;

public class DateFunctionAdapterTest {

    @Test
    public void testGenerateMaskedRow() {
        final DateVariance dv = new DateVariance();
        dv.setRandom(new Random(AllDataqualitySamplingTests.RANDOM_SEED));
        dv.parse("61", true);
        final List<String> patternList = Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd", "yyyy/MM/dd H:mm:ss", "AA9999");
        final DateFunctionAdapter function = new DateFunctionAdapter(dv, patternList);

        assertNull(function.generateMaskedRow(null)); // return null when input is null
        assertEquals("", function.generateMaskedRow("")); // return empty when input is empty
        assertEquals("  \t", function.generateMaskedRow("  \t")); // return original value when input contains only whitespaces

        assertEquals("2015-12-09", function.generateMaskedRow("2015-11-15")); // should mask
        assertEquals("2016/01/03", function.generateMaskedRow("2015/11/15")); // should mask
        assertEquals("2015/05/23 22:25:11", function.generateMaskedRow("2015/6/15 10:00:00"));
        assertEquals("14.2.1999", function.generateMaskedRow("22.3.1999"));
        assertEquals("5000*24*70", function.generateMaskedRow("2015*11*15")); // replace chars when no date pattern is applicable
        assertEquals("Vbrs-Kqc-260", function.generateMaskedRow("Vkfz-Zps-550"));
    }

    @Test
    public void testGenerateMaskedRowInConsistentMode() {
        final DateVariance dv = new DateVariance();
        dv.parse("31", true, null); // no need to pass a random object with fixed seed.
        final List<String> patternList = Arrays
                .asList(new String[] { "yyyy/MM/dd", "yyyy-MM-dd", "yyyy/MM/dd H:mm:ss", "AA9999" });
        final DateFunctionAdapter function = new DateFunctionAdapter(dv, patternList);
        function.setMaskingMode(FunctionMode.CONSISTENT);
        function.setSeed("azer1!");

        assertEquals(null, function.generateMaskedRow(null)); // return null when input is null
        assertEquals("", function.generateMaskedRow("")); // return empty when input is empty
        assertEquals("  \t", function.generateMaskedRow("  \t")); // return original value when input contains only whitespaces

        assertEquals("2015-11-08", function.generateMaskedRow("2015-11-15")); // should mask
        assertEquals("2015/10/21", function.generateMaskedRow("2015/11/15")); // should mask
        assertEquals("2015/06/22 3:54:55", function.generateMaskedRow("2015/6/15 10:00:00"));
        assertEquals("17.4.1999", function.generateMaskedRow("22.3.1999"));
        assertEquals("4493*91*51", function.generateMaskedRow("2015*11*15")); // replace chars when no date pattern is applicable
        assertEquals("Dhxu-Pud-099", function.generateMaskedRow("Vkfz-Zps-550"));
    }

}
