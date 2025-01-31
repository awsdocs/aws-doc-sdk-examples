// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.cloudwatch.scenario.CloudWatchActions;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAlarmsResponse;
import software.amazon.awssdk.services.cloudwatch.model.DeleteAnomalyDetectorResponse;
import software.amazon.awssdk.services.cloudwatch.model.DeleteDashboardsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatch.model.PutDashboardResponse;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataResponse;
import com.example.cloudwatch.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudWatchTest {
    private static CloudWatchClient cw;
    private static String namespace = "";
    private static String myDateSc = "";
    private static String costDateWeekSc = "";
    private static String dashboardNameSc = "";
    private static String dashboardJsonSc = "";
    private static String dashboardAddSc = "";
    private static String settingsSc = "";

    private static String alarmName = "";

    private static final CloudWatchActions cwActions = new CloudWatchActions();

    private static Dimension myDimension = null;

    private static final Logger logger = LoggerFactory.getLogger(CloudWatchTest.class);
    @BeforeAll
    public static void setUp() throws IOException {
        cw = CloudWatchClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        namespace = values.getNamespace();
        myDateSc = values.getMyDateSc();
        costDateWeekSc = values.getCostDateWeekSc();
        dashboardNameSc = values.getDashboardNameSc();
        dashboardJsonSc = values.getDashboardJsonSc();
        dashboardAddSc = values.getDashboardAddSc();
        settingsSc = values.getSettingsSc();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloService() {
        assertDoesNotThrow(() -> HelloService.listMets(cw, namespace));
        logger.info(" Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListNameSpaces() {
        assertDoesNotThrow(() -> {
            CompletableFuture<ArrayList<String>> future = cwActions.listNameSpacesAsync();
            ArrayList<String> list = future.join();
            assertFalse(list.isEmpty());
        });
        System.out.println("Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    void testCreateDashboard() {
        assertDoesNotThrow(() -> {
            CompletableFuture<PutDashboardResponse> future = cwActions.createDashboardWithMetricsAsync(dashboardNameSc, dashboardJsonSc);
            future.join();
        });
        logger.info("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetMetricData() {
        assertDoesNotThrow(() -> {
            CompletableFuture<GetMetricStatisticsResponse> future = cwActions.getMetricStatisticsAsync(costDateWeekSc);
            future.join();
        });
        logger.info("\n Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListDashboards() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.listDashboardsAsync();
            future.join();
        });
        logger.info("\n Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListMetrics() {
        Double dataPoint = Double.parseDouble("10.0");
        assertDoesNotThrow(() -> {
            CompletableFuture<PutMetricDataResponse> future = cwActions.createNewCustomMetricAsync(dataPoint);
            future.join();
        });
        logger.info("\n Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testMetricToDashboard() {
        assertDoesNotThrow(() -> {
            CompletableFuture<PutDashboardResponse> future = cwActions.addMetricToDashboardAsync(dashboardAddSc, dashboardNameSc);
            future.join();

        });
        logger.info("\n Test 10 passed");
    }
    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testCreateAlarm() {
        assertDoesNotThrow(() -> {
            CompletableFuture<String> future = cwActions.createAlarmAsync(settingsSc);
            alarmName = future.join();
            assertFalse(alarmName.isEmpty());
        });
        logger.info("\n Test 11 passed");
     }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDescribeAlarms() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.describeAlarmsAsync();
            future.join();
        });
        logger.info("\n Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testCustomMetricData() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.getCustomMetricDataAsync(settingsSc);
            future.join();
        });
        logger.info("\n Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testMetricDataForAlarm() {
        assertDoesNotThrow(() -> {
            CompletableFuture<PutMetricDataResponse> future = cwActions.addMetricDataForAlarmAsync(settingsSc);
            future.join();
        });
        logger.info("\n Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testMetricAlarmAsync() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.checkForMetricAlarmAsync(settingsSc);
            future.join();
        });
        logger.info("\n Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void testAlarmHistory() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.getAlarmHistoryAsync(settingsSc, myDateSc);
            future.join();
        });
        logger.info("\n Test 16 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testAnomalyDetector() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = cwActions.addAnomalyDetectorAsync(settingsSc);
            future.join();
        });
        logger.info("\n Test 17 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void testDeleteDashboard() {
        assertDoesNotThrow(() -> {
            CompletableFuture<DeleteDashboardsResponse> future = cwActions.deleteDashboardAsync(dashboardNameSc);
            future.join();

        });
        logger.info("\n Test 18 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void testCWAlarmAsync() {
        assertDoesNotThrow(() -> {
            CompletableFuture<DeleteAlarmsResponse> future = cwActions.deleteCWAlarmAsync(alarmName);
            future.join();

        });
        logger.info("\n Test 19 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
    public void testDeleteAnomalyDetector() {
        assertDoesNotThrow(() -> {
            CompletableFuture<DeleteAnomalyDetectorResponse> future = cwActions.deleteAnomalyDetectorAsync(settingsSc);
            future.join();

        });
        logger.info("\n Test 20 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/cloudwatch";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudwatch (an AWS Secrets Manager secret)")
    class SecretValues {
        private String logGroup;
        private String alarmName;
        private String instanceId;

        private String streamName;

        private String ruleResource;

        private String metricId;

        private String filterName;

        private String destinationArn;

        private String roleArn;

        private String ruleArn;

        private String filterPattern;

        private String ruleName;

        private String namespace;

        private String myDateSc;

        private String costDateWeekSc;

        private String dashboardNameSc;

        private String dashboardJsonSc;

        private String dashboardAddSc;

        private String settingsSc;

        private String metricImageSc;

        // Provide getter methods for each of the test values
        public String getLogGroup() {
            return logGroup;
        }

        public String getAlarmName() {
            return alarmName;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getStreamName() {
            return streamName;
        }

        public String getRuleResource() {
            return ruleResource;
        }

        public String getMetricId() {
            return metricId;
        }

        public String getFilterName() {
            return filterName;
        }

        public String getDestinationArn() {
            return destinationArn;
        }

        public String getRoleArn() {
            return roleArn;
        }

        public String getFilterPattern() {
            return filterPattern;
        }

        public String getRuleName() {
            return ruleName;
        }

        public String getRuleArn() {
            return ruleArn;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getMyDateSc() {
            return myDateSc;
        }

        public String getCostDateWeekSc() {
            return costDateWeekSc;
        }

        public String getDashboardNameSc() {
            return dashboardNameSc;
        }

        public String getDashboardJsonSc() {
            return dashboardJsonSc;
        }

        public String getDashboardAddSc() {
            return dashboardAddSc;
        }

        public String getSettingsSc() {
            return settingsSc;
        }

        public String getMetricImageSc() {
            return metricImageSc;
        }
    }
}
