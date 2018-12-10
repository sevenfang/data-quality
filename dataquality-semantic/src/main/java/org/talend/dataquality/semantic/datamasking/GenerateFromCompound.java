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

import com.mifmif.common.regex.Generex;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.lucene.document.Document;
import org.talend.dataquality.datamasking.functions.Function;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.talend.dataquality.semantic.model.CategoryType.COMPOUND;
import static org.talend.dataquality.semantic.model.CategoryType.DICT;
import static org.talend.dataquality.semantic.model.CategoryType.REGEX;

/**
 * data masking of a column with the content of compound semantic type
 */
public class GenerateFromCompound extends Function<String> {

    private Map<String, List<String>> valuesInDictionaries;

    private Map<String, String> regexs;

    private DictionarySnapshot dictionarySnapshot;

    private String semanticCategoryId;

    private transient Generex generex = null;

    private EnumeratedDistribution distribution;

    @Override
    protected String doGenerateMaskedField(String t) {
        DQCategory cat = dictionarySnapshot.getMetadata().get(semanticCategoryId);

        if (valuesInDictionaries == null && dictionarySnapshot != null) {
            valuesInDictionaries = new HashMap<>();
            regexs = new HashMap<>();

            initDictionaries(cat);
            processDistribution();

        }

        return getRandomValue();

    }

    private void initDictionaries(DQCategory cat) {
        cat.getChildren().forEach(child -> {
            CategoryType childType = child.getType();
            if (DICT.equals(childType)) {
                DQCategory childMetadata = dictionarySnapshot.getMetadata().get(child.getId());
                if (childMetadata != null) {
                    List<String> values = new ArrayList<>();
                    if (!childMetadata.getModified()) {
                        values.addAll(getValuesFromIndex(dictionarySnapshot.getSharedDataDict(), childMetadata.getId()));
                    } else {
                        values.addAll(getValuesFromIndex(dictionarySnapshot.getCustomDataDict(), childMetadata.getId()));
                    }
                    valuesInDictionaries.put(child.getId(), values);
                }
            } else if (REGEX.equals(childType)) {
                regexs.put(child.getId(), dictionarySnapshot.getRegexClassifier().getPatternStringByCategoryId(child.getId()));
            } else if (COMPOUND.equals(childType)) {
                initDictionaries(child);
            }
        });
    }

    private void processDistribution() {
        int largestDict = 1;
        int nbElem = 0;

        Collection<List<String>> values = valuesInDictionaries.values();
        if (values.size() > 0) {
            largestDict = values.stream().max(Comparator.comparing(List::size)).get().size();
            nbElem = values.stream().mapToInt(List::size).sum();
        }
        nbElem += regexs.size() * largestDict;

        int finalNbElem = nbElem;
        int finalLargestDict = largestDict;
        List<Pair<String, Double>> probabilities = new ArrayList<>();
        valuesInDictionaries.forEach((key, value) -> probabilities.add(new Pair(key, ((double) value.size() / finalNbElem))));
        regexs.forEach((key, value) -> probabilities.add(new Pair(key, (double) finalLargestDict / finalNbElem)));

        distribution = new EnumeratedDistribution(probabilities);
    }

    private String getRandomValue() {
        String result = EMPTY_STRING;

        String key = (String) distribution.sample();
        if (valuesInDictionaries.containsKey(key)) {
            List<String> values = valuesInDictionaries.get(key);
            result = values.get(rnd.nextInt(values.size()));
        } else if (regexs.containsKey(key)) {
            generex = new Generex(regexs.get(key));
            result = generex.random();
        }

        return result;
    }

    private List<String> getValuesFromIndex(Index index, String categoryId) {
        List<Document> listLuceneDocs = ((LuceneIndex) index).getSearcher().listDocumentsByCategoryId(categoryId, 0,
                Integer.MAX_VALUE);
        return listLuceneDocs.stream().flatMap(doc -> Arrays.asList(doc.getValues(DictionarySearcher.F_RAW)).stream())
                .collect(Collectors.toList());
    }

    @Override
    public void parse(String semanticCategoryId, boolean keepNullValues, Random rand) {
        this.semanticCategoryId = semanticCategoryId;

        setKeepNull(keepNullValues);
        if (rand != null) {
            setRandom(rand);
        }
    }

    public void setDictionarySnapshot(DictionarySnapshot dictionarySnapshot) {
        this.dictionarySnapshot = dictionarySnapshot;
    }
}
