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
package org.talend.dataquality.semantic.datamasking;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.datamasking.FunctionMode;
import org.talend.dataquality.datamasking.functions.KeysLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Function which used to generate data by regex
 */
public class GenerateFromRegexTest {

    /**
     * Test method for {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#setRandom(java.util.Random)}.
     */
    @Test
    public void testSetRandom() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.setRandom(null);
        assertNull("setRandom method will not modify the value of regexFunction", regexFunction.generex); //$NON-NLS-1$
        assertTrue("setRandom method will create new random instance", //$NON-NLS-1$
                regexFunction.getRandom() != null && regexFunction.getRandom() instanceof SecureRandom);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#parse(java.lang.String, boolean)}.
     * case 1 normal case
     */
    @Test
    public void testParseCase1() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", true); //$NON-NLS-1$
        Assert.assertNotNull("regexFunction.generex should not be null", regexFunction.generex); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#parse(java.lang.String, boolean)}.
     * case 2 extraParameter is null
     */
    @Test
    public void testParseCase2() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse(null, true);
        assertNull("regexFunction.generex should be null", regexFunction.generex); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 1 keepNull is true and inputValue is null
     */
    @Test
    public void testDoGenerateMaskedFieldStringCase1() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", true); //$NON-NLS-1$
        String maskResult = regexFunction.doGenerateMaskedField(null);
        assertNull("maskResult should be null", maskResult); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 2 keepNull is true and inputValue is empty
     */
    @Test
    public void testDoGenerateMaskedFieldStringCase2() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", true); //$NON-NLS-1$
        String maskResult = regexFunction.doGenerateMaskedField(StringUtils.EMPTY);
        assertEquals("maskResult should be EMPTY", StringUtils.EMPTY, maskResult); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 2 keepNull is true and inputValue is empty
     */
    @Test
    public void testDoGenerateMaskedFieldStringCaseConsistent() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.setSeed("12345");
        regexFunction.setMaskingMode(FunctionMode.CONSISTENT);
        regexFunction.parse("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", true); //$NON-NLS-1$
        String maskResult = regexFunction.generateMaskedRow("+33145263761", FunctionMode.CONSISTENT);
        String maskResult2 = regexFunction.generateMaskedRow("+33145263761", FunctionMode.CONSISTENT);
        assertEquals("+33 198-78 21 59", maskResult);
        assertEquals(maskResult2, maskResult);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 3 normal case
     */
    @Test
    public void testDoGenerateMaskedFieldStringCase3() {
        assertTrue(patternJudgeResult("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", "08 38 9302 63", new Random(12345), true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(patternJudgeResult("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", "*", new Random(), true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(patternJudgeResult("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", "*", new SecureRandom(), true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(patternJudgeResult("(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}", "*", null, true)); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(patternJudgeResult("^\\d*[02468]$", "355084", new Random(12345), true)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private boolean patternJudgeResult(String regexStr, String assertResult, Random random, boolean assertTrue) {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse(regexStr, true);
        regexFunction.setRandom(random);
        String maskResult = regexFunction.doGenerateMaskedField("any not empty value"); //$NON-NLS-1$
        Pattern compile = Pattern.compile(regexStr);
        Matcher matcher = compile.matcher(maskResult);

        assertEquals("maskResult is correct result:" + maskResult, matcher.matches(), assertTrue); //$NON-NLS-1$
        if (!"*".equals(assertResult)) { //$NON-NLS-1$
            assertEquals("maskResult is correct result: " + assertResult, assertResult, maskResult); //$NON-NLS-1$
        }
        return true;
    }

    private boolean patternJudgeResult(String regexStr, String assertResult, Random random, boolean assertTrue, String inputFile)
            throws NullPointerException, IOException, URISyntaxException {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse(regexStr, true);
        regexFunction.setRandom(random);
        Pattern compile = Pattern.compile(regexStr);

        List<String> aux = KeysLoader.loadKeys(this.getClass().getResource(inputFile).toURI().getPath().trim());
        String[] parameters = aux.toArray(new String[aux.size()]);
        for (String inputData : parameters) {
            if (!compile.matcher(inputData).matches()) {
                continue;
            }
            String maskResult = regexFunction.doGenerateMaskedField(inputData);
            Matcher matcher = compile.matcher(maskResult);

            assertEquals("maskResult is correct result:" + maskResult, matcher.matches(), assertTrue); //$NON-NLS-1$
            if (!"*".equals(assertResult)) { //$NON-NLS-1$
                Assert.assertEquals("maskResult is correct result: " + assertResult, assertResult, maskResult); //$NON-NLS-1$
            }
        }
        return true;
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 4 There are contains invalid character "^" and "$"
     */
    @Test
    public void testDoGenerateMaskedFieldStringCase4() {
        assertTrue(patternJudgeResult("^(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}$", "08 38 9302 63", new Random(12345), true)); //$NON-NLS-1$ //$NON-NLS-2$
        // more than one ^ and $
        assertTrue(patternJudgeResult("^^^^^^(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}$$$$$$$", "08 38 9302 63", //$NON-NLS-1$//$NON-NLS-2$
                new Random(12345), true));
        assertTrue(patternJudgeResult("^^^^\\^^(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}$$$$$\\$$", "^^+3339302 63 00$$$$$$", //$NON-NLS-1$//$NON-NLS-2$
                new Random(12345), false));
        assertTrue(
                patternJudgeResult("\\^^^^^^(0033 ?|\\+33 ?|0)[1-9]([-. ]?[0-9]{2}){4}$$$$$$\\$", "^^^^^^+33 4.31 02 3475$$$$$$$", //$NON-NLS-1$//$NON-NLS-2$
                        new Random(12345), false));
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#doGenerateMaskedField(java.lang.String)}.
     * case 5 400 items validation
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws NullPointerException
     */
    @Test
    public void testDoGenerateMaskedFieldStringCase5() throws NullPointerException, IOException, URISyntaxException {

        assertTrue(patternJudgeResult("^\\d*[02468]$", "*", new Random(12345), true, "numberData.txt")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Test method for {@link org.talend.dataquality.semantic.datamasking.GenerateFromRegex#isValidPattern(String)}.
     */
    @Test
    public void testIsValidPattern() {
        // US_PHONE case
        boolean isValidPattern = GenerateFromRegex.isValidPattern(
                "^(?:(?:(?:\\+|00)?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$");
        Assert.assertFalse("(?:pattern) is not support by this API by now", isValidPattern);
        // [UK_PHONE case
        isValidPattern = GenerateFromRegex.isValidPattern(
                "^(\\+44[[:space:]]?7[[:digit:]]{3}|\\(?07[[:digit:]]{3}\\)?)[[:space:]]?[[:digit:]]{3}[[:space:]]?[[:digit:]]{3}$");
        Assert.assertFalse("'[[:space:]]' and '[[:digit:]]' is not support by this API by now", isValidPattern);
        // DE_POSTAL_CODE case
        isValidPattern = GenerateFromRegex.isValidPattern("^(?!01000|99999)(0[1-9]\\d{3}|[1-9]\\d{4})$");
        Assert.assertFalse("'(?!pattern)' is not support by this API by now", isValidPattern);
        // (?=pattern) case
        isValidPattern = GenerateFromRegex.isValidPattern("^(?=01000|99999)(0[1-9]\\d{3}|[1-9]\\d{4})$");
        Assert.assertFalse("'(?=pattern)' is not support by this API by now", isValidPattern);
        isValidPattern = GenerateFromRegex.isValidPattern(null);
        Assert.assertFalse("null is not support by this API by now", isValidPattern);
        isValidPattern = GenerateFromRegex.isValidPattern("");
        assertTrue("empty should be support by this API by now", isValidPattern);
        isValidPattern = GenerateFromRegex.isValidPattern("^\\d*[02468]$");
        assertTrue("^\\d*[02468]$ should be support by this API by now", isValidPattern);
    }

    @Test
    public void testParseCaseTDQ16375() {
        GenerateFromRegex regexFunction = new GenerateFromRegex();
        regexFunction.parse("^alcool|bière|tequila|pastis|champagne$", true);
        regexFunction.setRandom(new Random(12345));
        String maskResult = regexFunction.doGenerateMaskedField("ABC");
        assertEquals("unexpected mask result! ", "pastis", maskResult);
    }

}