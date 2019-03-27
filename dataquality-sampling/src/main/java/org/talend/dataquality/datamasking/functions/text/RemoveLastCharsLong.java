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
 * created by jgonzalez on 22 juin 2015. See RemoveLastChars.
 *
 */
public class RemoveLastCharsLong extends RemoveLastChars<Long> {

    private static final long serialVersionUID = 3563086153882260632L;

    @Override
    protected Long getDefaultOutput() {
        return 0L;
    }

    @Override
    protected Long getOutput(String str) {
        return Long.parseLong(str);
    }
}
