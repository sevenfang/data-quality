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
import static org.talend.dataquality.semantic.utils.RegexUtils.removeInvalidCharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.apache.lucene.document.Document;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.semantic.Distribution;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import com.mifmif.common.regex.Generex;

/**
 * data masking of a column with the content of compound semantic type
 */
public class GenerateFromCompound extends Function<String> {

    private List<CategoryValues> categoryValues = null;

    private Distribution distribution;

    @Override
    protected String doGenerateMaskedField(String t) {
        return getRandomValue();
    }

    @Override
    public void parse(String semanticCategoryId, boolean keepNullValues, Random rand) {

        setKeepNull(keepNullValues);
        setRandom(rand);

        if (categoryValues != null) {
            processDistribution();
        }
    }

    private void processDistribution() {
        int largestDict;
        int nbElem;

        List<List<String>> values = new ArrayList<>();

        for (CategoryValues category : categoryValues) {
            if (DICT.equals(category.getType())) {
                values.add((List<String>) category.getValue()); //can't use stream because of cast
            }
        }

        long nbRegex = categoryValues.stream().filter(cat -> REGEX.equals(cat.getType())).count();
        if (values.size() > 0) {
            largestDict = values.stream().max(Comparator.comparing(List::size)).get().size();
            nbElem = (int) (values.stream().mapToInt(List::size).sum() + nbRegex * largestDict);
        } else {
            largestDict = 1;
            nbElem = (int) nbRegex;
        }

        List<Pair<String, Double>> probabilities = new ArrayList<>();

        categoryValues.forEach(categoryValues -> {

            if (DICT.equals(categoryValues.getType()))
                probabilities.add(
                        new Pair(categoryValues.getCategoryId(), ((double) ((List) categoryValues.getValue()).size() / nbElem)));
            else if (REGEX.equals(categoryValues.getType()))
                probabilities.add(new Pair(categoryValues.getCategoryId(), (double) largestDict / nbElem));
        });

        distribution = new Distribution(probabilities, rnd);
    }

    private String getRandomValue() {
        String result = EMPTY_STRING;

        String key = (String) distribution.sample();
        CategoryValues cats = categoryValues.stream().filter(cat -> key.equals(cat.getCategoryId())).findAny().get();

        CategoryType type = cats.getType();

        if (DICT.equals(type)) {
            List values = (List) cats.getValue();
            result = (String) values.get(rnd.nextInt(values.size()));
        } else if (REGEX.equals(type)) {
            result = ((Generex) cats.getValue()).random();

            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public void setCategoryValues(List<CategoryValues> categoryValues) {
        this.categoryValues = categoryValues;
    }
}
