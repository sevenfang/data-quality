package org.talend.dataquality.semantic;

import org.junit.Assert;
import org.talend.dataquality.semantic.datamasking.GenerateFromRegex;
import org.talend.dataquality.semantic.utils.RegexUtils;

public class RegexUtilsTests {

    /**
     * Test method for {@link org.talend.dataquality.semantic.utils.RegexUtils#removeInvalidCharacter(String)}.
     */
    // @Test
    // We commont this case because we don't want it execute every time.
    public void testremoveInvalidCharacterString() {
        GenerateFromRegex generateFromRegex = new GenerateFromRegex();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            RegexUtils.removeInvalidCharacter("^^^^^^^^^^^^aaaaa^^^^aaa$$$$aaa$$$aaa$$$$$$$$$$$$$");
        }
        long endTime = System.currentTimeMillis();
        Assert.assertTrue("All of action don't should spent more than 2 second. But in fact it is "
                + ((endTime - startTime) / 1000.0) + "s", endTime - startTime < 2000);
    }
}
