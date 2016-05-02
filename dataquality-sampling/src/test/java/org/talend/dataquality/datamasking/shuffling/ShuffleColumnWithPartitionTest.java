package org.talend.dataquality.datamasking.shuffling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShuffleColumnWithPartitionTest {

    private String file = "Shuffling_test_data.csv";

    private String file5000 = "Shuffling_test_data_5000.csv";

    private String file10000 = "Shuffling_test_data_10000.csv";

    private String file20000 = "Shuffling_test_data_20000.csv";

    private String file50000 = "Shuffling_test_data_50000.csv";

    private String file100000 = "Shuffling_test_data_100000.csv";

    private static List<Integer> group = new ArrayList<Integer>();

    private static List<List<Integer>> numColumn = new ArrayList<List<Integer>>();

    private static GenerateData generator = new GenerateData();

    private ShuffleColumnWithPartition partition = new ShuffleColumnWithPartition();

    @BeforeClass
    public static void prepareData() {
        group.add(6);
        group.add(7);
        group.add(8);

        List<Integer> column1 = Arrays.asList(new Integer[] { 0, 1 });
        List<Integer> column2 = Arrays.asList(new Integer[] { 3 });
        numColumn.add(column1);
        numColumn.add(column2);
    }

    @Test
    public void testshuffleColumnDataByGroup() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file);
        List<List<Object>> fileData = generator.getTableValue(file);
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> zipSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> zipL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object zipS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            zipSL.add(zipS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object zip = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            zipL.add(zip);
        }

        for (int i = 0; i < fileData.size(); i++) {

            Object zip = zipL.get(i);

            int firstZip = zipL.indexOf(zip);
            int lastZip = zipL.lastIndexOf(zip);

            // test whether the city and the zip code are unique
            if (firstZip == lastZip) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) idL.get(i));
                String fncmp = (String) firstNameL.get(i);
                String emailcmp = (String) emailL.get(i);
                String citycmp = (String) cityL.get(i);
                String zipcmp = (String) zipL.get(i);

                int idscmp = Integer.parseInt((String) idSL.get(i));
                String fnscmp = (String) firstNameSL.get(i);
                String emailscmp = (String) emailSL.get(i);
                String cityscmp = (String) citySL.get(i);
                String zipscmp = (String) zipSL.get(i);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(zipcmp, zipscmp);

            } else if (firstZip != lastZip && i == firstZip) {
                // zip code has several records

                String zipcmp = (String) zipL.get(i);

                List<Integer> rIndex = new ArrayList<Integer>();
                for (int row = firstZip; row <= lastZip; row++) {
                    if (zipcmp.equals((String) zipSL.get(row))) {
                        rIndex.add(row);
                    }
                }

                if (rIndex.size() > 2) {

                    for (int row : rIndex) {
                        String citycmp = (String) cityL.get(row);
                        zipcmp = (String) zipL.get(row);

                        String fnscmp = (String) firstNameSL.get(row);
                        String emailscmp = (String) emailSL.get(row);
                        String cityscmp = (String) citySL.get(row);
                        String zipscmp = (String) zipSL.get(row);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(zipcmp, zipscmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(row));
                        Assert.assertTrue(originalIndex != row);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != row);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String zipcmpE = (String) zipL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(zipcmpE, zipscmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    @Test
    public void testshuffleColumnDataByGroup5000() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file5000);
        List<List<Object>> fileData = generator.getTableValue(file5000);
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);

        List<Row> fileRowShuffled = new ArrayList<Row>();
        List<Row> fileRow = new ArrayList<Row>();

        for (int i = 0; i < fileData.size(); i++) {
            fileRowShuffled.add(new Row(i, fileDataShuffled.get(i), fileDataShuffled.get(i).subList(6, 9)));
            fileRow.add(new Row(i, fileData.get(i), fileData.get(i).subList(6, 9)));
        }

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> stateSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> stateL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object stateS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            stateSL.add(stateS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object state = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            stateL.add(state);
        }

        for (int i = 0; i < fileData.size(); i++) {
            Row row = fileRow.get(i);
            int firstGroup = fileRow.indexOf(row);
            int lastGroup = fileRow.lastIndexOf(row);

            // test whether the city and the zip code are unique
            if (firstGroup == lastGroup) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) fileRow.get(i).rItems.get(0));
                String fncmp = (String) fileRow.get(i).rItems.get(1);
                String emailcmp = (String) fileRow.get(i).rItems.get(3);
                String citycmp = (String) fileRow.get(i).rItems.get(6);
                String statecmp = (String) fileRow.get(i).rItems.get(7);

                int idscmp = Integer.parseInt((String) fileRowShuffled.get(i).rItems.get(0));
                String fnscmp = (String) fileRowShuffled.get(i).rItems.get(1);
                String emailscmp = (String) fileRowShuffled.get(i).rItems.get(3);
                String cityscmp = (String) fileRowShuffled.get(i).rItems.get(6);
                String statescmp = (String) fileRowShuffled.get(i).rItems.get(7);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(statecmp, statescmp);

            } else if (firstGroup != lastGroup && i == firstGroup) {
                // zip code has several records
                List<Integer> rIndex = new ArrayList<Integer>();
                for (int j = firstGroup; j <= lastGroup; j++) {
                    if (fileRow.get(i).equals(fileRowShuffled.get(j))) {
                        rIndex.add(j);
                    }
                }

                if (rIndex.size() > 2) {
                    for (int rowI : rIndex) {
                        String citycmp = (String) fileRow.get(rowI).rItems.get(6);
                        String statecmp = (String) fileRow.get(rowI).rItems.get(7);

                        String fnscmp = (String) fileRowShuffled.get(rowI).rItems.get(1);
                        String emailscmp = (String) fileRowShuffled.get(rowI).rItems.get(3);
                        String cityscmp = (String) fileRowShuffled.get(rowI).rItems.get(6);
                        String statescmp = (String) fileRowShuffled.get(rowI).rItems.get(7);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(statecmp, statescmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(rowI));
                        Assert.assertTrue(originalIndex != rowI);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != rowI);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String statecmpE = (String) stateL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(statecmpE, statecmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    @Test
    public void testshuffleColumnDataByGroup10000() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file10000);
        List<List<Object>> fileData = generator.getTableValue(file10000);
        long startTime = System.currentTimeMillis();
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);
        long endTime = System.currentTimeMillis();
        System.out.println("10000 rows " + (endTime - startTime));

        List<Row> fileRowShuffled = new ArrayList<Row>();
        List<Row> fileRow = new ArrayList<Row>();

        for (int i = 0; i < fileData.size(); i++) {
            fileRowShuffled.add(new Row(i, fileDataShuffled.get(i), fileDataShuffled.get(i).subList(6, 9)));
            fileRow.add(new Row(i, fileData.get(i), fileData.get(i).subList(6, 9)));
        }

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> stateSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> stateL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object stateS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            stateSL.add(stateS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object state = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            stateL.add(state);
        }

        for (int i = 0; i < fileData.size(); i++) {
            Row row = fileRow.get(i);
            int firstGroup = fileRow.indexOf(row);
            int lastGroup = fileRow.lastIndexOf(row);

            // test whether the city and the zip code are unique
            if (firstGroup == lastGroup) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) fileRow.get(i).rItems.get(0));
                String fncmp = (String) fileRow.get(i).rItems.get(1);
                String emailcmp = (String) fileRow.get(i).rItems.get(3);
                String citycmp = (String) fileRow.get(i).rItems.get(6);
                String statecmp = (String) fileRow.get(i).rItems.get(7);

                int idscmp = Integer.parseInt((String) fileRowShuffled.get(i).rItems.get(0));
                String fnscmp = (String) fileRowShuffled.get(i).rItems.get(1);
                String emailscmp = (String) fileRowShuffled.get(i).rItems.get(3);
                String cityscmp = (String) fileRowShuffled.get(i).rItems.get(6);
                String statescmp = (String) fileRowShuffled.get(i).rItems.get(7);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(statecmp, statescmp);

            } else if (firstGroup != lastGroup && i == firstGroup) {
                // zip code has several records
                List<Integer> rIndex = new ArrayList<Integer>();
                for (int j = firstGroup; j <= lastGroup; j++) {
                    if (fileRow.get(i).equals(fileRowShuffled.get(j))) {
                        rIndex.add(j);
                    }
                }

                if (rIndex.size() > 2) {
                    for (int rowI : rIndex) {
                        String citycmp = (String) fileRow.get(rowI).rItems.get(6);
                        String statecmp = (String) fileRow.get(rowI).rItems.get(7);

                        String fnscmp = (String) fileRowShuffled.get(rowI).rItems.get(1);
                        String emailscmp = (String) fileRowShuffled.get(rowI).rItems.get(3);
                        String cityscmp = (String) fileRowShuffled.get(rowI).rItems.get(6);
                        String statescmp = (String) fileRowShuffled.get(rowI).rItems.get(7);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(statecmp, statescmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(rowI));
                        Assert.assertTrue(originalIndex != rowI);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != rowI);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String statecmpE = (String) stateL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(statecmpE, statecmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    @Test
    public void testshuffleColumnDataByGroup20000() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file20000);
        List<List<Object>> fileData = generator.getTableValue(file20000);
        long startTime = System.currentTimeMillis();
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);
        long endTime = System.currentTimeMillis();
        System.out.println("20000 rows " + (endTime - startTime));

        List<Row> fileRowShuffled = new ArrayList<Row>();
        List<Row> fileRow = new ArrayList<Row>();

        for (int i = 0; i < fileData.size(); i++) {
            fileRowShuffled.add(new Row(i, fileDataShuffled.get(i), fileDataShuffled.get(i).subList(6, 9)));
            fileRow.add(new Row(i, fileData.get(i), fileData.get(i).subList(6, 9)));
        }

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> stateSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> stateL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object stateS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            stateSL.add(stateS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object state = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            stateL.add(state);
        }

        for (int i = 0; i < fileData.size(); i++) {
            Row row = fileRow.get(i);
            int firstGroup = fileRow.indexOf(row);
            int lastGroup = fileRow.lastIndexOf(row);

            // test whether the city and the zip code are unique
            if (firstGroup == lastGroup) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) fileRow.get(i).rItems.get(0));
                String fncmp = (String) fileRow.get(i).rItems.get(1);
                String emailcmp = (String) fileRow.get(i).rItems.get(3);
                String citycmp = (String) fileRow.get(i).rItems.get(6);
                String statecmp = (String) fileRow.get(i).rItems.get(7);

                int idscmp = Integer.parseInt((String) fileRowShuffled.get(i).rItems.get(0));
                String fnscmp = (String) fileRowShuffled.get(i).rItems.get(1);
                String emailscmp = (String) fileRowShuffled.get(i).rItems.get(3);
                String cityscmp = (String) fileRowShuffled.get(i).rItems.get(6);
                String statescmp = (String) fileRowShuffled.get(i).rItems.get(7);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(statecmp, statescmp);

            } else if (firstGroup != lastGroup && i == firstGroup) {
                // zip code has several records
                List<Integer> rIndex = new ArrayList<Integer>();
                for (int j = firstGroup; j <= lastGroup; j++) {
                    if (fileRow.get(i).equals(fileRowShuffled.get(j))) {
                        rIndex.add(j);
                    }
                }

                if (rIndex.size() > 2) {
                    for (int rowI : rIndex) {
                        String citycmp = (String) fileRow.get(rowI).rItems.get(6);
                        String statecmp = (String) fileRow.get(rowI).rItems.get(7);

                        String fnscmp = (String) fileRowShuffled.get(rowI).rItems.get(1);
                        String emailscmp = (String) fileRowShuffled.get(rowI).rItems.get(3);
                        String cityscmp = (String) fileRowShuffled.get(rowI).rItems.get(6);
                        String statescmp = (String) fileRowShuffled.get(rowI).rItems.get(7);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(statecmp, statescmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(rowI));
                        Assert.assertTrue(originalIndex != rowI);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != rowI);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String statecmpE = (String) stateL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(statecmpE, statecmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    @Test
    public void testshuffleColumnDataByGroup50000() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file50000);
        List<List<Object>> fileData = generator.getTableValue(file50000);
        long startTime = System.currentTimeMillis();
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);
        long endTime = System.currentTimeMillis();
        System.out.println("50000 rows " + (endTime - startTime));

        List<Row> fileRowShuffled = new ArrayList<Row>();
        List<Row> fileRow = new ArrayList<Row>();

        for (int i = 0; i < fileData.size(); i++) {
            fileRowShuffled.add(new Row(i, fileDataShuffled.get(i), fileDataShuffled.get(i).subList(6, 9)));
            fileRow.add(new Row(i, fileData.get(i), fileData.get(i).subList(6, 9)));
        }

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> stateSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> stateL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object stateS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            stateSL.add(stateS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object state = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            stateL.add(state);
        }

        for (int i = 0; i < fileData.size(); i++) {
            Row row = fileRow.get(i);
            int firstGroup = fileRow.indexOf(row);
            int lastGroup = fileRow.lastIndexOf(row);

            // test whether the city and the zip code are unique
            if (firstGroup == lastGroup) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) fileRow.get(i).rItems.get(0));
                String fncmp = (String) fileRow.get(i).rItems.get(1);
                String emailcmp = (String) fileRow.get(i).rItems.get(3);
                String citycmp = (String) fileRow.get(i).rItems.get(6);
                String statecmp = (String) fileRow.get(i).rItems.get(7);

                int idscmp = Integer.parseInt((String) fileRowShuffled.get(i).rItems.get(0));
                String fnscmp = (String) fileRowShuffled.get(i).rItems.get(1);
                String emailscmp = (String) fileRowShuffled.get(i).rItems.get(3);
                String cityscmp = (String) fileRowShuffled.get(i).rItems.get(6);
                String statescmp = (String) fileRowShuffled.get(i).rItems.get(7);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(statecmp, statescmp);

            } else if (firstGroup != lastGroup && i == firstGroup) {
                // zip code has several records
                List<Integer> rIndex = new ArrayList<Integer>();
                for (int j = firstGroup; j <= lastGroup; j++) {
                    if (fileRow.get(i).equals(fileRowShuffled.get(j))) {
                        rIndex.add(j);
                    }
                }

                if (rIndex.size() > 2) {
                    for (int rowI : rIndex) {
                        String citycmp = (String) fileRow.get(rowI).rItems.get(6);
                        String statecmp = (String) fileRow.get(rowI).rItems.get(7);

                        String fnscmp = (String) fileRowShuffled.get(rowI).rItems.get(1);
                        String emailscmp = (String) fileRowShuffled.get(rowI).rItems.get(3);
                        String cityscmp = (String) fileRowShuffled.get(rowI).rItems.get(6);
                        String statescmp = (String) fileRowShuffled.get(rowI).rItems.get(7);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(statecmp, statescmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(rowI));
                        Assert.assertTrue(originalIndex != rowI);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != rowI);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String statecmpE = (String) stateL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(statecmpE, statecmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    @Test
    public void testshuffleColumnDataByGroup100000() {
        List<List<Object>> fileDataShuffled = generator.getTableValue(file100000);
        List<List<Object>> fileData = generator.getTableValue(file100000);
        long startTime = System.currentTimeMillis();
        partition.shuffleColumnByGroup(fileDataShuffled, numColumn, Arrays.asList(new String[] { "talend", "computer" }), group);
        long endTime = System.currentTimeMillis();
        System.out.println("100000 rows " + (endTime - startTime));

        List<Row> fileRowShuffled = new ArrayList<Row>();
        List<Row> fileRow = new ArrayList<Row>();

        for (int i = 0; i < fileData.size(); i++) {
            fileRowShuffled.add(new Row(i, fileDataShuffled.get(i), fileDataShuffled.get(i).subList(6, 9)));
            fileRow.add(new Row(i, fileData.get(i), fileData.get(i).subList(6, 9)));
        }

        List<Object> idSL = new ArrayList<Object>();
        List<Object> firstNameSL = new ArrayList<Object>();
        List<Object> emailSL = new ArrayList<Object>();
        List<Object> citySL = new ArrayList<Object>();
        List<Object> stateSL = new ArrayList<Object>();

        List<Object> idL = new ArrayList<Object>();
        List<Object> firstNameL = new ArrayList<Object>();
        List<Object> emailL = new ArrayList<Object>();
        List<Object> cityL = new ArrayList<Object>();
        List<Object> stateL = new ArrayList<Object>();

        for (int i = 0; i < fileData.size(); i++) {
            Object idS = fileDataShuffled.get(i).get(0);
            Object firstNameS = fileDataShuffled.get(i).get(1);
            Object emailS = fileDataShuffled.get(i).get(3);
            Object cityS = fileDataShuffled.get(i).get(6);
            Object stateS = fileDataShuffled.get(i).get(7);

            idSL.add(idS);
            firstNameSL.add(firstNameS);
            emailSL.add(emailS);
            citySL.add(cityS);
            stateSL.add(stateS);

            Object id = fileData.get(i).get(0);
            Object firstName = fileData.get(i).get(1);
            Object email = fileData.get(i).get(3);
            Object city = fileData.get(i).get(6);
            Object state = fileData.get(i).get(7);

            idL.add(id);
            firstNameL.add(firstName);
            emailL.add(email);
            cityL.add(city);
            stateL.add(state);
        }

        for (int i = 0; i < fileData.size(); i++) {
            Row row = fileRow.get(i);
            int firstGroup = fileRow.indexOf(row);
            int lastGroup = fileRow.lastIndexOf(row);

            // test whether the city and the zip code are unique
            if (firstGroup == lastGroup) {
                // only one record in the table, checks whether the information retains the same
                int idcmp = Integer.parseInt((String) fileRow.get(i).rItems.get(0));
                String fncmp = (String) fileRow.get(i).rItems.get(1);
                String emailcmp = (String) fileRow.get(i).rItems.get(3);
                String citycmp = (String) fileRow.get(i).rItems.get(6);
                String statecmp = (String) fileRow.get(i).rItems.get(7);

                int idscmp = Integer.parseInt((String) fileRowShuffled.get(i).rItems.get(0));
                String fnscmp = (String) fileRowShuffled.get(i).rItems.get(1);
                String emailscmp = (String) fileRowShuffled.get(i).rItems.get(3);
                String cityscmp = (String) fileRowShuffled.get(i).rItems.get(6);
                String statescmp = (String) fileRowShuffled.get(i).rItems.get(7);

                Assert.assertEquals(idcmp, idscmp);
                Assert.assertEquals(fncmp, fnscmp);
                Assert.assertEquals(emailcmp, emailscmp);
                Assert.assertEquals(citycmp, cityscmp);
                Assert.assertEquals(statecmp, statescmp);

            } else if (firstGroup != lastGroup && i == firstGroup) {
                // zip code has several records
                List<Integer> rIndex = new ArrayList<Integer>();
                for (int j = firstGroup; j <= lastGroup; j++) {
                    if (fileRow.get(i).equals(fileRowShuffled.get(j))) {
                        rIndex.add(j);
                    }
                }

                if (rIndex.size() > 2) {
                    for (int rowI : rIndex) {
                        String citycmp = (String) fileRow.get(rowI).rItems.get(6);
                        String statecmp = (String) fileRow.get(rowI).rItems.get(7);

                        String fnscmp = (String) fileRowShuffled.get(rowI).rItems.get(1);
                        String emailscmp = (String) fileRowShuffled.get(rowI).rItems.get(3);
                        String cityscmp = (String) fileRowShuffled.get(rowI).rItems.get(6);
                        String statescmp = (String) fileRowShuffled.get(rowI).rItems.get(7);

                        // test whether city changes
                        Assert.assertEquals(citycmp, cityscmp);

                        // test whether zip code changes
                        Assert.assertEquals(statecmp, statescmp);

                        // test whether the id and the first name change the position
                        int originalIndex = idL.indexOf(idSL.get(rowI));
                        Assert.assertTrue(originalIndex != rowI);

                        // test whether id the first name change the group
                        Assert.assertEquals(firstNameL.get(originalIndex), fnscmp);

                        // test whether the email changes the position
                        int originalIndexE = emailL.indexOf(emailscmp);
                        Assert.assertTrue(originalIndexE != rowI);

                        // test whether the mail's group changes the group
                        String citycmpE = (String) cityL.get(originalIndexE);
                        String statecmpE = (String) stateL.get(originalIndexE);
                        Assert.assertEquals(citycmpE, cityscmp);
                        Assert.assertEquals(statecmpE, statecmp);

                        // test whether the (id first-name) remains the information of email
                        String emailO = (String) emailL.get(originalIndex);
                        Assert.assertTrue(!emailO.equals(emailscmp));
                    }
                }

            }
        }

    }

    class Row implements Comparable<Row> {

        int rIndex;

        List<Object> rGroup = new ArrayList<Object>();

        List<Object> rItems = new ArrayList<Object>();

        public Row(int rIndex, List<Object> rItems, List<Object> rGroup) {
            super();
            this.rIndex = rIndex;
            for (Object o : rItems) {
                this.rItems.add(o);
            }

            if (rGroup == null) {
                this.rGroup = null;
            } else {
                for (Object o : rGroup) {
                    this.rGroup.add(o);
                }
            }

        }

        @Override
        public String toString() {
            return "( " + rIndex + " " + " rItems " + rItems + " rGroup " + rGroup + " )";
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Row)) {
                return false;
            }
            Row r = (Row) o;
            boolean equal = true;
            for (int i = 0; i < rGroup.size(); i++) {
                if (!rGroup.get(i).equals(r.rGroup.get(i))) {
                    equal = false;
                }
            }

            return equal;
        }

        @Override
        public int compareTo(Row r) {
            int max = (rGroup.size() <= r.rGroup.size()) ? rGroup.size() : r.rGroup.size();
            int cmp = -1;
            for (int i = 0; i < max; i++) {
                cmp = ((String) rGroup.get(i)).compareTo(((String) r.rGroup.get(i)));
                if (cmp != 0) {
                    return cmp;
                }
            }

            return cmp;
        }

        Object getItem(int index) {
            return rItems.get(index);
        }

    }

}
