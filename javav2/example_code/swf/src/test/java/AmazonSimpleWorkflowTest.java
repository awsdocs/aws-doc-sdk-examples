/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.helloswf.ActivityWorker;
import com.example.helloswf.SWFWorkflowDemo;
import com.example.helloswf.WorkflowStarter;
import com.example.helloswf.WorkflowWorker;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
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
    private static SwfClient swf ;
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        domain = values.getDomain()+ java.util.UUID.randomUUID();
        taskList = values.getTaskList();
        workflow = values.getWorkflow();
        workflowVersion = values.getWorkflowVersion();
        activity = values.getActivity();
        activityVersion = values.getActivityVersion();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonSimpleWorkflowTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            // Populate the data members required for all tests
            prop.load(input);
            domain = prop.getProperty("domain")+ java.util.UUID.randomUUID();
            taskList = prop.getProperty("taskList");
            workflow = prop.getProperty("workflow");
            workflowVersion = prop.getProperty("workflowVersion");
            activity = prop.getProperty("activity");
            activityVersion = prop.getProperty("activityVersion");


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void registerDomain() {
        assertDoesNotThrow(() ->SWFWorkflowDemo.registerDomain(swf, domain));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void RegisterWorkflowType() {
        assertDoesNotThrow(() ->SWFWorkflowDemo.registerWorkflowType(swf, domain, workflow, workflowVersion, taskList));
        System.out.println("Test 2 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
   public void registerActivityType() {
        assertDoesNotThrow(() ->SWFWorkflowDemo.registerActivityType(swf, domain, activity, activityVersion, taskList));
        System.out.println("Test 3 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
   public void WorkflowStarter() {
       assertDoesNotThrow(() ->WorkflowStarter.startWorkflow(swf, workflowInput, domain, workflow,workflowVersion));
       System.out.println("Test 4 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
   public void WorkflowWorker(){
        assertDoesNotThrow(() ->WorkflowWorker.pollADecision(swf, domain, taskList, activity, activityVersion));
        System.out.println("Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
   public void ActivityWorker() {
       assertDoesNotThrow(() ->ActivityWorker.getPollData(swf, domain, taskList));
       System.out.println("Test 6 passed");
   }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

