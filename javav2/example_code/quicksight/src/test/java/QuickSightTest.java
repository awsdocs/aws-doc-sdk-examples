/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.quicksight.QuickSightClient;
import org.junit.jupiter.api.*;
import com.example.quicksight.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
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
    public static void setUp() {
        qsClient = QuickSightClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        account = values.getAccount();
        analysisId = values.getAnalysisId();
        dashboardId = values.getDashboardId();
        templateId = values.getTemplateId();
        dataSetArn = values.getDataSetArn();
        analysisArn = values.getAnalysisArn();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = QuickSightTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            account = prop.getProperty("account");
            analysisId = prop.getProperty("analysisId");
            dashboardId = prop.getProperty("dashboardId");
            templateId = prop.getProperty("templateId");
            dataSetArn = prop.getProperty("dataSetArn");
            analysisArn = prop.getProperty("analysisArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void DescribeAnalysis() {
        assertDoesNotThrow(() ->DescribeAnalysis.describeSpecificAnalysis(qsClient, account, analysisId));
        System.out.println("DescribeAnalysis test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeDashboard() {
        assertDoesNotThrow(() ->DescribeDashboard.describeSpecificDashboard(qsClient, account, dashboardId));
        System.out.println("DescribeDashboard test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeTemplate() {
        assertDoesNotThrow(() ->DescribeTemplate.describeSpecificTemplate(qsClient, account, templateId));
        System.out.println("DescribeTemplate test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListThemes() {
        assertDoesNotThrow(() ->ListThemes.listAllThemes(qsClient, account));
        System.out.println("ListThemes test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListAnalyses() {
        assertDoesNotThrow(() ->ListAnalyses.listAllAnAnalyses(qsClient, account));
        System.out.println("ListAnalyses test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ListDashboards() {
        assertDoesNotThrow(() ->ListDashboards.listAllDashboards(qsClient, account));
        System.out.println("ListDashboards test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListTemplates() {
        assertDoesNotThrow(() ->ListTemplates.listAllTemplates(qsClient, account));
        System.out.println("ListTemplates test passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void UpdateDashboard() {
        assertDoesNotThrow(() ->UpdateDashboard.updateSpecificDashboard(qsClient, account, dashboardId, dataSetArn, analysisArn));
        System.out.println("UpdateDashboard test passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/quicksight";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/quicksight (an AWS Secrets Manager secret)")
    class SecretValues {
        private String account;
        private String analysisId;
        private String dashboardId;

        private String templateId;

        private String dataSetArn;

        private String analysisArn;

        public String getAccount() {
            return account;
        }

        public String getAnalysisId() {
            return analysisId;
        }

        public String getDashboardId() {
            return dashboardId;
        }

        public String getTemplateId() {
            return templateId;
        }

        public String getDataSetArn() {
            return dataSetArn;
        }

        public String getAnalysisArn() {
            return analysisArn;
        }
    }
}

