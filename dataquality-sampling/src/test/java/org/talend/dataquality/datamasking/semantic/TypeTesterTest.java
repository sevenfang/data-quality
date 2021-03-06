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

import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.datamasking.TypeTester;

/**
 * Test for TypeTester class
 */
public class TypeTesterTest {

    private static TypeTester typeTester = null;

    /**
     * Init instance of TypeTester
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        typeTester = new TypeTester();
    }

    /**
     * Release instance of TypeTester
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        typeTester = null;
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.lang.Integer)}.
     */
    @Test
    public void testGetTypeInteger() {
        Integer inputData = 10;
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Integer so that result should same with 0", 0, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Integer so that result should same with 0", 0, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.lang.Long)}.
     */
    @Test
    public void testGetTypeLong() {
        Long inputData = 32768L;
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Long so that result should same with 1", 1, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Long so that result should same with 1", 1, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.lang.Float)}.
     */
    @Test
    public void testGetTypeFloat() {
        Float inputData = 0.1f;
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Float so that result should same with 2", 2, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Float so that result should same with 2", 2, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.lang.Double)}.
     */
    @Test
    public void testGetTypeDouble() {
        Double inputData = 0.1d;
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Double so that result should same with 3", 3, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Double so that result should same with 3", 3, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.lang.String)}.
     */
    @Test
    public void testGetTypeString() {
        String inputData = "This is a string";//$NON-NLS-1$
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is String so that result should same with 4", 4, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is String so that result should same with 4", 4, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getType(java.util.Date)}.
     */
    @Test
    public void testGetTypeDate() {
        Date inputData = new Date();
        int result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Date so that result should same with 5", 5, result); //$NON-NLS-1$

        inputData = null;
        result = typeTester.getType(inputData);
        Assert.assertEquals("The input data type is Date so that result should same with 5", 5, result); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dataquality.datamasking.TypeTester#getTypeByName(java.lang.String)}.
     */
    @Test
    public void testGetTypeByName() {
        String typeName = "test"; //$NON-NLS-1$
        int result = typeTester.getTypeByName(typeName);
        Assert.assertEquals("The typeName is " + typeName + ". It is not exist so that result should same with -1", -1, result); //$NON-NLS-1$ //$NON-NLS-2$

        typeName = null;
        result = typeTester.getTypeByName(typeName);
        Assert.assertEquals("The typeName is " + typeName + ". It is not exist so that result should same with -1", -1, result); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
