package org.talend.dataquality.datamasking.functions.email;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class MaskEmailLocalPartByXTest {

    private String output;

    private String mail = "çéhœ.h啊hش@uestc.edu.cn";

    private String spemail = "hehe@telecom-bretagne.eu";

    private MaskEmailLocalPartByX maskEmailLocalPartByX = new MaskEmailLocalPartByX();

    @Test
    public void testEmpty() {
        maskEmailLocalPartByX.setKeepEmpty(true);
        output = maskEmailLocalPartByX.generateMaskedRow("");
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void test1Good() {
        maskEmailLocalPartByX.parse("", false);
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void testNullParameter() {
        maskEmailLocalPartByX.parse(null, false);
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void testSpecialEmail() {
        maskEmailLocalPartByX.parse("", true);
        output = maskEmailLocalPartByX.generateMaskedRow(spemail);
        Assert.assertEquals("XXXX@telecom-bretagne.eu", output);

    }

    @Test
    public void test2WithInput() {
        maskEmailLocalPartByX.parse("hehe", false);
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneCharacter() {
        maskEmailLocalPartByX.parse("A", false);
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("AAAAAAAAA@uestc.edu.cn", output);
    }

    @Test
    public void test2WithOneDigit() {
        maskEmailLocalPartByX.parse("1", false);
        output = maskEmailLocalPartByX.generateMaskedRow(mail);
        Assert.assertEquals("XXXXXXXXX@uestc.edu.cn", output);
    }

    @Test
    public void test3NullEmail() {
        maskEmailLocalPartByX.parse("", false);
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test3KeepNullEmail() {
        maskEmailLocalPartByX.parse("", true);
        output = maskEmailLocalPartByX.generateMaskedRow(null);
        Assert.assertTrue(output == null);
    }

    @Test
    public void test4EmptyEmail() {
        maskEmailLocalPartByX.parse("", false);
        output = maskEmailLocalPartByX.generateMaskedRow("");
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    public void test5WrongFormat() {
        maskEmailLocalPartByX.parse("", false);
        output = maskEmailLocalPartByX.generateMaskedRow("hehe");
        Assert.assertEquals("XXXX", output);
    }
}
