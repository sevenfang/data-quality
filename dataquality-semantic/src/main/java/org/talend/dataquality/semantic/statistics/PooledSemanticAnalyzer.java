// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.semantic.statistics;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.talend.dataquality.common.inference.Analyzer;
import org.talend.dataquality.semantic.recognizer.BulkCategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizer;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;

/**
 * Semantic type infer executor. <br>
 * 
 * @see Analyzer
 * 
 */
public class PooledSemanticAnalyzer extends SemanticAnalyzer {

    private static final long serialVersionUID = 6808620909722453108L;

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public PooledSemanticAnalyzer(CategoryRecognizerBuilder builder) {
        super(builder);
    }

    public PooledSemanticAnalyzer(CategoryRecognizerBuilder builder, int limit) {
        super(builder, limit);
    }

    /**
     * Analyze the record by guessing the data semantic type.
     */
    @Override
    public boolean analyze(String... record) {
        results.resize(record.length);
        resizeCategoryRecognizer(record);
        if (currentCount < limit || limit <= 0) {
            for (int i = 0; i < record.length; i++) {
                BulkCategoryRecognizer categoryRecognizer = (BulkCategoryRecognizer) columnIdxToCategoryRecognizer.get(i);
                if (categoryRecognizer == null) {
                    throw new RuntimeException("CategoryRecognizer is null for record and i=" + i + " " + Arrays.asList(record));
                } else {
                    categoryRecognizer.process(record[i], threadPool);
                }
            }
            currentCount++;
        }
        return true;
    }

    @Override
    public void end() {
        super.end();
        for (CategoryRecognizer catRecognizer : columnIdxToCategoryRecognizer.values()) {
            ((BulkCategoryRecognizer) catRecognizer).sendLastData(threadPool);
        }

        threadPool.shutdown();

        // Blocks until all tasks have completed execution after a shutdown request
        try {

            System.out.println("call awaitTermination");
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("KO");
            }
        } catch (InterruptedException e) {
            System.out.println("All elasticSearch tasks didn't finished. " + e.getMessage());
        }

        for (CategoryRecognizer catRecognizer : columnIdxToCategoryRecognizer.values()) {
            catRecognizer.end();
        }

    }

}
