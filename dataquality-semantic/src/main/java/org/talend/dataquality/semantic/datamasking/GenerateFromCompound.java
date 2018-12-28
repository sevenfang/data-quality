// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import static org.talend.dataquality.semantic.model.CategoryType.DICT;
import static org.talend.dataquality.semantic.model.CategoryType.REGEX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.DateVariance;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.datamasking.semantic.DateFunctionAdapter;
import org.talend.dataquality.datamasking.semantic.FluctuateNumericString;
import org.talend.dataquality.datamasking.semantic.ReplaceCharactersWithGeneration;
import org.talend.dataquality.semantic.Distribution;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.model.CategoryType;

import com.mifmif.common.regex.Generex;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.statistics.SemanticQualityAnalyzer;

/**
 * data masking of a column with the content of compound semantic type
 */
public class GenerateFromCompound extends Function<String> {

    private Logger LOGGER = LoggerFactory.getLogger(GenerateFromCompound.class);

    private List<CategoryValues> categoryValues = null;

    private DictionarySnapshot dictionarySnapshot = null;

    private String dataType;

    @Override
    protected String doGenerateMaskedField(String value) {
        String result = EMPTY_STRING;
        Optional<List<CategoryValues>> categoryValues = findMatchTypes(value);
        if (categoryValues.isPresent()) {
            Distribution distribution = processDistribution(categoryValues.get());
            try {
                result = getMaskedValue(value, distribution);
            } catch (IllegalAccessException | InstantiationException e) {
                LOGGER.info(e.getMessage(), e);
            }
        } //if category is not present, it's not a valid value and it won't be processed here

        return result;

    }

    private Optional<List<CategoryValues>> findMatchTypes(String value) {
        List<CategoryValues> categoryValuesResult = new ArrayList<>();
        Optional<List<CategoryValues>> result = Optional.empty();

        for (CategoryValues category : categoryValues) {
            if (DICT.equals(category.getType())) {
                SemanticQualityAnalyzer analyzer = new SemanticQualityAnalyzer(dictionarySnapshot,
                        new String[] { category.getName() });
                if (analyzer.isValid(dictionarySnapshot.getDQCategoryByName(category.getName()), value))
                    categoryValuesResult.add(category); //can't use stream because of cast
            }
        }

        categoryValues.stream().filter(cat -> REGEX.equals(cat.getType())).forEach(cat -> {
            if (value.matches((String) cat.getValue()))
                categoryValuesResult.add(cat);
        });

        if (categoryValuesResult.size() > 0)
            result = Optional.of(categoryValuesResult);

        return result;
    }

    @Override
    public void parse(String semanticCategoryId, boolean keepNullValues, Random rand) {
        setKeepNull(keepNullValues);
        setRandom(rand);
    }

    private Distribution processDistribution(List<CategoryValues> categories) {
        int largestDict;
        int nbElem;

        List<List<String>> values = new ArrayList<>();

        for (CategoryValues category : categories) {
            if (DICT.equals(category.getType())) {
                values.add((List<String>) category.getValue()); //can't use stream because of cast
            }
        }

        long nbRegex = categories.stream().filter(cat -> REGEX.equals(cat.getType())).count();
        if (values.size() > 0) {
            largestDict = values.stream().max(Comparator.comparing(List::size)).get().size();
            nbElem = (int) (values.stream().mapToInt(List::size).sum() + nbRegex * largestDict);
        } else {
            largestDict = 1;
            nbElem = (int) nbRegex;
        }

        List<Pair<String, Double>> probabilities = new ArrayList<>();

        categories.forEach(categoryValues -> {
            if (DICT.equals(categoryValues.getType()))
                probabilities.add(
                        new Pair(categoryValues.getCategoryId(), ((double) ((List) categoryValues.getValue()).size() / nbElem)));
            else if (REGEX.equals(categoryValues.getType()))
                probabilities.add(new Pair(categoryValues.getCategoryId(), (double) largestDict / nbElem));
        });

        return new Distribution(probabilities, rnd);
    }

    private String getMaskedValue(String value, Distribution distribution) throws IllegalAccessException, InstantiationException {
        String result = EMPTY_STRING;

        String key = (String) distribution.sample();
        CategoryValues cats = categoryValues.stream().filter(cat -> key.equals(cat.getCategoryId())).findAny().get();

        final MaskableCategoryEnum cat = MaskableCategoryEnum.getCategoryById(cats.getName());
        if (cat != null) { //specific masking
            Function<String> function = (Function<String>) cat.getFunctionType().getClazz().newInstance();
            function.parse(cat.getParameter(), true, getRandom());
            function.setKeepFormat(true);
            result = function.generateMaskedRow(value);
        } else {
            CategoryType type = cats.getType();
            if (DICT.equals(type)) {
                List values = (List) cats.getValue();
                result = (String) values.get(rnd.nextInt(values.size()));
            } else if (REGEX.equals(type)) {
                Generex generex = new Generex((String) cats.getValue(), getRandom());
                result = generex.random();
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    public void setCategoryValues(List<CategoryValues> categoryValues) {
        this.categoryValues = categoryValues;
    }

    public void setDictionarySnapshot(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }
}
