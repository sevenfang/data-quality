package org.talend.dataquality.semantic.validator.impl;

import com.mifmif.common.regex.Generex;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.talend.dataquality.semantic.classifier.custom.UserDefinedClassifier;
import org.talend.dataquality.semantic.datamasking.model.CategoryValues;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.model.DQRegEx;
import org.talend.dataquality.semantic.model.DQValidator;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;
import org.talend.dataquality.semantic.validator.GenerateValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.talend.dataquality.semantic.model.CategoryType.DICT;
import static org.talend.dataquality.semantic.model.CategoryType.REGEX;

public class GenerateValidatorTest {

    private DictionarySearcher mockSearcher;

    private LuceneIndex mockIndex;

    private DictionarySnapshot dictionarySnapshot;

    @Before
    public void setUp() {
        mockSearcher = Mockito.mock(DictionarySearcher.class);
        mockIndex = Mockito.mock(LuceneIndex.class);
        when(mockIndex.getSearcher()).thenReturn(mockSearcher);
        when(mockSearcher.listDocumentsByCategoryId("1.1")).thenReturn(getDocuments("1", 9999));
        when(mockSearcher.listDocumentsByCategoryId("1.2")).thenReturn(getDocuments("2", 1));

        dictionarySnapshot = Mockito.mock(DictionarySnapshot.class);
        when(dictionarySnapshot.getMetadata()).thenReturn(createDictionarySnapshot());
        when(dictionarySnapshot.getSharedDataDict()).thenReturn(mockIndex);
        when(dictionarySnapshot.getCustomDataDict()).thenReturn(mockIndex);

    }

    @Test
    public void initSemanticTypes() {
        List<CategoryValues> values = GenerateValidator.initSemanticTypes(dictionarySnapshot, createCategoryWithChildDict(),
                null);

        assertEquals(1, values.size());
        assertEquals(9999, ((List) values.get(0).getValue()).size());
    }

    @Test
    public void initSemanticTypesWithRegEx() {
        UserDefinedClassifier regExClassifier = Mockito.mock(UserDefinedClassifier.class);
        when(regExClassifier.getPatternStringByCategoryId("1.1")).thenReturn("[0-9]");
        when(dictionarySnapshot.getMetadata()).thenReturn(createDictionarySnapshotWithRegex());
        when(dictionarySnapshot.getRegexClassifier()).thenReturn(regExClassifier);

        List<CategoryValues> values = GenerateValidator.initSemanticTypes(dictionarySnapshot, createCategoryWithChildRegex(),
                null);

        assertEquals(1, values.size());
        assertTrue(values.get(0).getValue() instanceof Generex);
    }

    private DQCategory createCategoryWithChildDict() {
        DQCategory cat = new DQCategory();

        List<DQCategory> children = new ArrayList<>();

        DQCategory child = new DQCategory();
        child.setId("1.1");
        child.setType(DICT);
        children.add(child);

        cat.setChildren(children);
        return cat;
    }

    private DQCategory createCategoryWithChildRegex() {
        DQCategory cat = new DQCategory();

        DQValidator validator = new DQValidator();
        validator.setPatternString("[0-9]");

        DQRegEx regEx = new DQRegEx();
        regEx.setValidator(validator);

        DQCategory child = new DQCategory();
        child.setId("1.1");
        child.setType(REGEX);
        child.setRegEx(regEx);

        List<DQCategory> children = new ArrayList<>();
        children.add(child);

        cat.setChildren(children);
        return cat;
    }

    private Map<String, DQCategory> createDictionarySnapshot() {
        Map<String, DQCategory> snapshot = new HashMap<>();

        DQCategory catChild1 = createCategory("1.1", DICT, false);
        DQCategory catChild2 = createCategory("1.2", DICT, false);

        DQCategory cat = new DQCategory();
        cat.setId("1");
        cat.setType(CategoryType.COMPOUND);
        cat.setChildren(Arrays.asList(catChild1, catChild2));

        snapshot.put("1", cat);
        snapshot.put("1.1", catChild1);
        snapshot.put("1.2", catChild2);

        return snapshot;
    }

    private Map<String, DQCategory> createDictionarySnapshotWithRegex() {
        Map<String, DQCategory> snapshot = new HashMap<>();

        DQCategory catChild1 = createCategory("1.1", REGEX, false);
        DQCategory catChild2 = createCategory("1.2", DICT, true);

        DQCategory cat = new DQCategory();
        cat.setId("1");
        cat.setType(CategoryType.COMPOUND);
        cat.setChildren(Arrays.asList(catChild1, catChild2));

        snapshot.put("1", cat);
        snapshot.put("1.1", catChild1);
        snapshot.put("1.2", catChild2);

        return snapshot;
    }

    private List<Document> getDocuments(String id, int number) {
        List<Document> documents = new ArrayList<>();

        Document doc = new Document();
        for (int i = 0; i < number; i++)
            doc.add(new StringField(DictionarySearcher.F_RAW, "fRaw" + id + i, Field.Store.YES));

        documents.add(doc);

        return documents;
    }

    private DQCategory createCategory(String id, CategoryType type, boolean modified) {
        DQCategory cat = new DQCategory();
        cat.setId(id);
        cat.setType(type);
        cat.setModified(modified);

        return cat;
    }
}
