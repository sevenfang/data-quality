package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

public class BulkCategoryRecognizer extends DefaultCategoryRecognizer {

    private int batchSize = 100;

    private List<String> inputList = new ArrayList<String>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(5);

    private boolean isCacheActivated = false;

    public BulkCategoryRecognizer(Index dictionary, Index keyword) throws IOException {
        super(dictionary, keyword);
    }

    @Override
    public String[] process(String data) {
        total++;
        Set<String> categories = getSubCategorySet(data);
        if (categories.size() > 0) {
            for (String catId : categories) {
                DQCategory meta = crm.getCategoryMetadataByName(catId);
                incrementCategory(catId, meta == null ? catId : meta.getLabel());
            }
        } else {
            // do not increment any category
        }
        return categories.toArray(new String[categories.size()]);
    }

    /**
     * @param data the input value
     * @return the set of its semantic categories
     */
    @Override
    public Set<String> getSubCategorySet(String data) {
        if (data == null || StringUtils.EMPTY.equals(data.trim())) {
            emptyCount++;
            return new HashSet<>();
        }
        final Set<String> knownCategory = knownCategoryCache.get(data);
        if (knownCategory != null) {
            return knownCategory;
        }

        MainCategory mainCategory = MainCategory.getMainCategory(data);
        Set<String> subCategorySet = new HashSet<>();

        switch (mainCategory) {
        case Alpha:
        case AlphaNumeric:
            // subCategorySet.addAll(dataDictFieldClassifier.classify(data));
            processNew(data);
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            if (isCacheActivated) {
                knownCategoryCache.put(data, subCategorySet);
            }
            break;
        case Numeric:
            if (userDefineClassifier != null) {
                subCategorySet.addAll(userDefineClassifier.classify(data, mainCategory));
            }
            if (isCacheActivated) {
                knownCategoryCache.put(data, subCategorySet);
            }
            break;
        case NULL:
        case BLANK:
            emptyCount++;
            break;
        case UNKNOWN:
            break;
        }
        return subCategorySet;

    }

    private String[] processNew(String data) {

        inputList.add(data);
        if (inputList.size() % batchSize == 0) {
            sendBatchQueryToES(new ArrayList<String>(inputList));
            System.out.println("submit " +total +" "+ inputList.get(inputList.size() - 1));
            inputList.clear();
        }
        return new String[0];
    }

    private void sendBatchQueryToES(List<String> data) {
        FutureTask<String> task = new BatchFutureTask(new BatchQueryCallable(data), this);
        threadPool.submit(task);
    }

    void incrementCategory(List<String> cats) {
        System.out.println("increment categories " + cats);
    }

    public Collection<CategoryFrequency> getResult() {
        if (inputList.size() > 0) {
            sendBatchQueryToES(inputList);
            System.out.println("submit " + inputList.get(inputList.size() - 1));
        }

        threadPool.shutdown();
        return super.getResult();
    }

    class BatchFutureTask extends FutureTask<String> {

        private BulkCategoryRecognizer bulkCategoryRecognizer;

        private BatchQueryCallable callable;

        public BatchFutureTask(BatchQueryCallable callable, BulkCategoryRecognizer bulkCategoryRecognizer) {
            super(callable);
            this.callable = callable;
            this.bulkCategoryRecognizer = bulkCategoryRecognizer;
        }

        @Override
        public void done() {
            // callback
            bulkCategoryRecognizer.incrementCategory(callable.getCategories());
        }

    }

    class BatchQueryCallable implements Callable<String> {

        private List<String> inputList;

        private List<String> categories = new ArrayList<String>();

        public BatchQueryCallable(List<String> inputList) {
            this.inputList = inputList;
        }

        @Override
        public String call() {
            try {
                // ESIndex().send(inputList)
                String lastCellName = inputList.get(inputList.size() - 1);
                System.out.println("start handling " + lastCellName);
                long delay = Math.round(Math.random() * 100);
                try {
                    Thread.currentThread().sleep(delay);
                    System.out.println("finished " + lastCellName + "\t in " + delay + " ms");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                categories.add(lastCellName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "abc";
        }

        public List<String> getCategories() {
            return categories;
        }

    }
}
