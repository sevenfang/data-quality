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
package org.talend.dataquality.datamasking.functions;

/**
 * created by jgonzalez on 22 juin 2015. This function will replace every character by the parameter.
 *
 */
public class ReplaceAll extends CharactersOperation<String> {

    private static final long serialVersionUID = -6755455022090241272L;

    @Override
    protected String getDefaultOutput() {
        return EMPTY_STRING;
    }

    @Override
    protected String getOutput(String str) {
        return str;
    }

    @Override
    protected void initAttributes() {
        if (parameters.length > 0)
            super.charToReplace = parameters[0].charAt(0);
    }

    @Override
    protected boolean validParameters() {
        return parameters.length == 0 || (parameters.length == 1 && patternCharacter.matcher(parameters[0]).matches());
    }
}
