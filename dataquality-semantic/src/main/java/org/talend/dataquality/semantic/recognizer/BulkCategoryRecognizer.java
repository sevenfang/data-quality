package org.talend.dataquality.semantic.recognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.talend.dataquality.semantic.index.Index;
import org.talend.dataquality.semantic.model.DQCategory;

public class BulkCategoryRecognizer extends DefaultCategoryRecognizer {

    private int batchSize = 100;

    private List<String> inputList = new ArrayList<String>();

    private boolean isCacheActivated = false;

    private int totalRecords = 0;

    public BulkCategoryRecognizer(Index dictionary, Index keyword) throws IOException {
        super(dictionary, keyword);
    }

    public String[] process(String data, ExecutorService threadPool) {
        total++;
        Set<String> categories = getSubCategorySet(data, threadPool);
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
     * @param threadPool
     * @return the set of its semantic categories
     */
    private Set<String> getSubCategorySet(String data, ExecutorService threadPool) {
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
            processNew(data, threadPool);
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

    private String[] processNew(String data, ExecutorService threadPool) {
        totalRecords++;
        inputList.add(data);
        if (inputList.size() % batchSize == 0) {
            sendBatchQueryToES(new ArrayList<String>(inputList), threadPool);
            inputList.clear();
        }
        return new String[0];
    }

    private void sendBatchQueryToES(List<String> data, ExecutorService threadPool) {
        FutureTask<Boolean> task = new BatchFutureTask(new BatchQueryCallable(data), this);
        threadPool.submit(task);
        // System.out.println("submitted " + totalRecords);
    }

    void incrementCategory(List<String> cats) {
        // System.out.println("increment categories " + cats);
        for (String catId : cats) {
            super.incrementCategory(catId);
        }
    }

    @Override
    public void reset() {
        super.reset();
        inputList.clear();
    }

    public void sendLastData(ExecutorService threadPool) {
        if (!inputList.isEmpty()) {
            sendBatchQueryToES(new ArrayList<String>(inputList), threadPool);
            inputList.clear();
        }
    }

    class BatchFutureTask extends FutureTask<Boolean> {

        private BulkCategoryRecognizer bulkCategoryRecognizer;

        private BatchQueryCallable callable;

        public BatchFutureTask(BatchQueryCallable callable, BulkCategoryRecognizer bulkCategoryRecognizer) {
            super(callable, true);
            this.callable = callable;
            this.bulkCategoryRecognizer = bulkCategoryRecognizer;
        }

        @Override
        public void done() {
            // callback

            // System.out.println("done " + (totalRecords));
            bulkCategoryRecognizer.incrementCategory(callable.getCategories());
        }

    }

    class BatchQueryCallable implements Runnable {

        private List<String> inputList;

        private List<String> categories = new ArrayList<String>();

        public BatchQueryCallable(List<String> inputList) {
            this.inputList = inputList;
        }

        @Override
        public void run() {
            for (Pair<String, Set<String>> pair : dataDictFieldClassifier.multipleClassify(inputList)) {
                categories.addAll(pair.getRight());
            }
            // try {
            //
            // dataDictFieldClassifier.multipleClassify(inputList);
            // // ESIndex().send(inputList)
            // String lastCellName = inputList.get(inputList.size() - 1);
            // System.out.println("start handling " + lastCellName);
            // long delay = Math.round(Math.random() * 100);
            // try {
            // Thread.currentThread().sleep(delay);
            // System.out.println("finished " + lastCellName + "\t in " + delay + " ms");
            // } catch (InterruptedException e) {
            // System.out.println(e.getMessage());
            // }
            // categories.add(lastCellName);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }

        }

        public List<String> getCategories() {
            return categories;
        }

    }
}
