// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(QuickSightTest.class);
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
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testDescribeAnalysis() {
        assertDoesNotThrow(() -> DescribeAnalysis.describeSpecificAnalysis(qsClient, account, analysisId));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeDashboard() {
        assertDoesNotThrow(() -> DescribeDashboard.describeSpecificDashboard(qsClient, account, dashboardId));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeTemplate() {
        assertDoesNotThrow(() -> DescribeTemplate.describeSpecificTemplate(qsClient, account, templateId));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListThemes() {
        assertDoesNotThrow(() -> ListThemes.listAllThemes(qsClient, account));
        logger.info("Test 4  passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListAnalyses() {
        assertDoesNotThrow(() -> ListAnalyses.listAllAnAnalyses(qsClient, account));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListDashboards() {
        assertDoesNotThrow(() -> ListDashboards.listAllDashboards(qsClient, account));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testListTemplates() {
        assertDoesNotThrow(() -> ListTemplates.listAllTemplates(qsClient, account));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testUpdateDashboard() {
        assertDoesNotThrow(
                () -> UpdateDashboard.updateSpecificDashboard(qsClient, account, dashboardId, dataSetArn, analysisArn));
        logger.info("Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
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
