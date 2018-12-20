package org.talend.dataquality.semantic.datamasking;

import com.mifmif.common.regex.Generex;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GenerateFromCompoundTest {

    private GenerateFromCompound generate;

    @Before
    public void setUp() {
        generate = new GenerateFromCompound();
    }

    @Test
    public void doGenerateMaskedFieldWithDistribution() {
        generate.setCategoryValues(createCategoryValuesWithDict());
        generate.parse("1", true, new Random(1234));
        String result = generate.doGenerateMaskedField("a");
        assertEquals("value3", result);
    }

    @Test
    public void doGenerateMaskedFieldWithRegex() {
        generate.setCategoryValues(createCategoryValuesWithRegex());
        generate.parse("1", true, null);
        String result = generate.doGenerateMaskedField("a");
        assertEquals("3", result);
    }

    private List<CategoryValues> createCategoryValuesWithDict() {
        List<CategoryValues> values = new ArrayList<>();

        CategoryValues dict = new CategoryValues();
        dict.setCategoryId("1");
        dict.setType(CategoryType.DICT);
        dict.setValue(Arrays.asList("value1", "value2", "value3"));

        values.add(dict);

        return values;
    }

    private List<CategoryValues> createCategoryValuesWithRegex() {
        List<CategoryValues> values = new ArrayList<>();

        Generex generex = new Generex("[0-999]$", new Random(1234));
        CategoryValues regex = new CategoryValues();
        regex.setCategoryId("1");
        regex.setType(CategoryType.REGEX);
        regex.setValue(generex);

        values.add(regex);

        return values;
    }
}
