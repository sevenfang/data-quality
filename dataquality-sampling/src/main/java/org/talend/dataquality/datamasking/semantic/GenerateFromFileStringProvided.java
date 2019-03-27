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
package org.talend.dataquality.datamasking.semantic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.datamasking.functions.KeysLoader;
import org.talend.dataquality.datamasking.functions.generation.GenerateFromFileString;

public class GenerateFromFileStringProvided extends GenerateFromFileString {

    private static final long serialVersionUID = 8936060786451303843L;

    private static final Logger LOG = LoggerFactory.getLogger(GenerateFromFileStringProvided.class);

    /**
     * The code should't come here if yes a file name will be appand to genericTokens in the parent method.
     * That is not we want.
     */
    // TODO We need to remove this method after known we can
    @Override
    public void parse(String extraParameter, boolean keepNullValues) {
        super.parse(extraParameter, keepNullValues);
        try {
            genericTokens = KeysLoader.loadKeys(getClass().getResourceAsStream(parameters[0]));
        } catch (IOException | NullPointerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String generateMaskedRow(String t) {
        if (t == null || EMPTY_STRING.equals(t.trim())) {
            return t;
        }
        return doGenerateMaskedField(t);
    }

    @Override
    protected String doGenerateMaskedField(String str) {
        return super.doGenerateMaskedField(str);
    }
}
