// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.helloswf.ActivityWorker;
import com.example.helloswf.SWFWorkflowDemo;
import com.example.helloswf.WorkflowStarter;
import com.example.helloswf.WorkflowWorker;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.swf.SwfClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonSimpleWorkflowTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonSimpleWorkflowTest.class);
    private static SwfClient swf;
    private static String workflowInput = "";
    private static String domain = "";
    private static String taskList = "";
    private static String workflow = "";
    private static String workflowVersion = "";
    private static String activity = "";
    private static String activityVersion = "";

    @BeforeAll
    public static void setUp() {
        Region region = software.amazon.awssdk.regions.Region.US_EAST_1;
        swf = SwfClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        domain = values.getDomain() + java.util.UUID.randomUUID();
        taskList = values.getTaskList();
        workflow = values.getWorkflow();
        workflowVersion = values.getWorkflowVersion();
        activity = values.getActivity();
        activityVersion = values.getActivityVersion();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testRegisterDomain() {
        assertDoesNotThrow(() -> SWFWorkflowDemo.registerDomain(swf, domain));
        logger.info("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testRegisterWorkflowType() {
        assertDoesNotThrow(
                () -> SWFWorkflowDemo.registerWorkflowType(swf, domain, workflow, workflowVersion, taskList));
        logger.info("\nTest 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testRegisterActivityType() {
        assertDoesNotThrow(
                () -> SWFWorkflowDemo.registerActivityType(swf, domain, activity, activityVersion, taskList));
        logger.info("\nTest 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testWorkflowStarter() {
        assertDoesNotThrow(() -> WorkflowStarter.startWorkflow(swf, workflowInput, domain, workflow, workflowVersion));
        logger.info("\nTest 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testWorkflowWorker() {
        assertDoesNotThrow(() -> WorkflowWorker.pollADecision(swf, domain, taskList, activity, activityVersion));
        logger.info("\nTest 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testActivityWorker() {
        assertDoesNotThrow(() -> ActivityWorker.getPollData(swf, domain, taskList));
        logger.info("\nTest 6 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/swf";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/swf (an AWS Secrets Manager secret)")
    class SecretValues {
        private String domain;
        private String taskList;
        private String workflow;

        private String workflowVersion;

        private String activity;

        private String activityVersion;

        public String getDomain() {
            return domain;
        }

        public String getTaskList() {
            return taskList;
        }

        public String getWorkflow() {
            return workflow;
        }

        public String getWorkflowVersion() {
            return workflowVersion;
        }

        public String getActivity() {
            return activity;
        }

        public String getActivityVersion() {
            return activityVersion;
        }
    }
}
