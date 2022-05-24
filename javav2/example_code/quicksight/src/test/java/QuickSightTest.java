/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import org.junit.jupiter.api.*;
import com.example.quicksight.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class QuickSightTest {

    private static QuickSightClient qsClient;
    private static String account = "";
    private static String analysisId = "";
    private static String dashboardId = "";
    private static String templateId = "";
    private static String dataSetArn = "";
    private static String analysisArn = "";

    @BeforeAll
    public static void setUp() throws IOException {

        qsClient = QuickSightClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = QuickSightTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            account = prop.getProperty("account");
            analysisId = prop.getProperty("analysisId");
            dashboardId = prop.getProperty("dashboardId");
            templateId = prop.getProperty("templateId");
            dataSetArn = prop.getProperty("dataSetArn");
            analysisArn = prop.getProperty("analysisArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(qsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void DescribeAnalysis() {
        DescribeAnalysis.describeSpecificAnalysis(qsClient, account, analysisId);
        System.out.println("Test 2 passed");
    }


    @Test
    @Order(3)
    public void DescribeDashboard() {
        DescribeDashboard.describeSpecificDashboard(qsClient, account, dashboardId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeTemplate() {
        DescribeTemplate.describeSpecificTemplate(qsClient, account, templateId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListThemes() {
        ListThemes.listAllThemes(qsClient, account);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListAnalyses() {
        ListAnalyses.listAllAnAnalyses(qsClient, account);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListDashboards() {
        ListDashboards.listAllDashboards(qsClient, account);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void ListTemplates() {
        ListTemplates.listAllTemplates(qsClient, account);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void UpdateDashboard() {
        UpdateDashboard.updateSpecificDashboard(qsClient, account, dashboardId, dataSetArn, analysisArn);
        System.out.println("Test 9 passed");
    }
}
