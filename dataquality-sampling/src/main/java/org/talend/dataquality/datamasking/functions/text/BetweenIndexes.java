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
package org.talend.dataquality.datamasking.functions.text;

/**
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is either BetweenIndexesKeep,
 * BetweenIndexesRemove or BetweenIndexesReplace. It will set the bounds of the indexes according to the input length.
 *
 */
public abstract class BetweenIndexes extends CharactersOperation<String> {

    private static final long serialVersionUID = 1114307514352123034L;

    @Override
    protected String getDefaultOutput() {
        return EMPTY_STRING;
    }

    @Override
    protected String getOutput(String str) {
        return str;
    }
}
