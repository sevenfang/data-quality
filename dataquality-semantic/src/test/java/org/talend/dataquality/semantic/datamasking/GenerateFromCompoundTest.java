package org.talend.dataquality.semantic.datamasking;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.dataquality.semantic.index.DictionarySearcher;
import org.talend.dataquality.semantic.index.LuceneIndex;
import org.talend.dataquality.semantic.model.CategoryType;
import org.talend.dataquality.semantic.model.DQCategory;
import org.talend.dataquality.semantic.snapshot.DictionarySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GenerateFromCompoundTest {

    private GenerateFromCompound generate;

    private DictionarySnapshot dictionarySnapshot;

    private DictionarySearcher mockSearcher;

    @Before
    public void setUp() {
        generate = new GenerateFromCompound();

        mockSearcher = Mockito.mock(DictionarySearcher.class);
        LuceneIndex mockIndex = Mockito.mock(LuceneIndex.class);
        when(mockIndex.getSearcher()).thenReturn(mockSearcher);
        when(mockSearcher.listDocumentsByCategoryId("1.1", 0, Integer.MAX_VALUE)).thenReturn(getDocuments("1", 9999));
        when(mockSearcher.listDocumentsByCategoryId("1.2", 0, Integer.MAX_VALUE)).thenReturn(getDocuments("2", 1));

        dictionarySnapshot = Mockito.mock(DictionarySnapshot.class);
        when(dictionarySnapshot.getMetadata()).thenReturn(createDictionarySnapshot());
        when(dictionarySnapshot.getSharedDataDict()).thenReturn(mockIndex);
        when(dictionarySnapshot.getCustomDataDict()).thenReturn(mockIndex);
        generate.setDictionarySnapshot(dictionarySnapshot);
    }

    @Test
    public void doGenerateMaskedField() {
        generate.parse("1", true, null);
        String result = generate.doGenerateMaskedField("a");
        assertTrue(result.startsWith("fRaw1"));
    }

    @Test
    public void doGenerateMaskedFieldWithDistribution() {
        when(mockSearcher.listDocumentsByCategoryId("1.1", 0, Integer.MAX_VALUE)).thenReturn(getDocuments("1", 1));
        when(mockSearcher.listDocumentsByCategoryId("1.2", 0, Integer.MAX_VALUE)).thenReturn(getDocuments("2", 9999));

        generate.parse("1", true, null);
        String result = generate.doGenerateMaskedField("a");
        assertTrue(result.startsWith("fRaw2"));
    }

    private Map<String, DQCategory> createDictionarySnapshot() {
        Map<String, DQCategory> snapshot = new HashMap<>();

        DQCategory catChild1 = new DQCategory();
        catChild1.setId("1.1");
        catChild1.setType(CategoryType.DICT);
        catChild1.setModified(false);

        DQCategory catChild2 = new DQCategory();
        catChild2.setId("1.2");
        catChild2.setType(CategoryType.DICT);
        catChild1.setModified(true);

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
}
