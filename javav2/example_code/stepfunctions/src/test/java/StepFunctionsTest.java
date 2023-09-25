/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.stepfunctions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sfn.SfnClient;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StepFunctionsTest {
    private static  SfnClient sfnClient;
    private static String  roleNameSC = "";
    private static String  activityNameSC = "";
    private static String  stateMachineNameSC = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_EAST_1;
        sfnClient = SfnClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        roleNameSC = values.getRoleNameSC()+ java.util.UUID.randomUUID();;
        activityNameSC = values.getActivityNameSC()+ java.util.UUID.randomUUID();;
        stateMachineNameSC = values.getStateMachineNameSC()+ java.util.UUID.randomUUID();;

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = StepFunctionsTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            roleNameSC = prop.getProperty("roleNameSC")+ java.util.UUID.randomUUID();;
            activityNameSC = prop.getProperty("activityNameSC")+ java.util.UUID.randomUUID();;
            stateMachineNameSC = prop.getProperty("stateMachineNameSC")+ java.util.UUID.randomUUID();;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void ListActivities() {
        assertDoesNotThrow(() ->ListActivities.listAllActivites(sfnClient));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void TestHello() {
        assertDoesNotThrow(() ->HelloStepFunctions.listMachines(sfnClient));
        System.out.println("Test 2 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void TestSTFMVP() throws Exception {
        Region regionGl = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
            .region(regionGl)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        Scanner sc = new Scanner(System.in);
        boolean action = false ;
        String polJSON = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Sid\": \"\",\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Principal\": {\n" +
            "                \"Service\": \"states.amazonaws.com\"\n" +
            "            },\n" +
            "            \"Action\": \"sts:AssumeRole\"\n" +
            "        }\n" +
            "    ]\n" +
            "}" ;

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Create an activity.");
        String activityArn = StepFunctionsScenario.createActivity(sfnClient, activityNameSC);
        System.out.println("The ARN of the activity is "+activityArn);
        System.out.println(StepFunctionsScenario.DASHES);
        InputStream input = StepFunctionsScenario.class.getClassLoader().getResourceAsStream("chat_sfn_state_machine.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readValue(input, JsonNode.class);
        String jsonString = mapper.writeValueAsString(jsonNode);

        // Modify the Resource node.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonString);
        ((ObjectNode) root.path("States").path("GetInput")).put("Resource", activityArn);

        // Convert the modified Java object back to a JSON string.
        String stateDefinition = objectMapper.writeValueAsString(root);
        System.out.println(stateDefinition);

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Create a state machine.");
        String roleARN = StepFunctionsScenario.createIAMRole(iam, roleNameSC, polJSON );
        String stateMachineArn = StepFunctionsScenario.createMachine(sfnClient, roleARN, stateMachineNameSC, stateDefinition);
        System.out.println("The ARN of the state machine is "+stateMachineArn);
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Describe the state machine.");
        StepFunctionsScenario.describeStateMachine(sfnClient, stateMachineArn);
        System.out.println("What should ChatSFN call you?");
        String userName = "Foo";
        System.out.println("Hello "+userName);
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println(StepFunctionsScenario.DASHES);
        // The json information to pass to the StartExecution call.
        String executionJson = "{ \"name\" : \""+userName +"\" }";
        System.out.println(executionJson);
        System.out.println("Start execution of the state machine and interact with it.");
        String runArn = StepFunctionsScenario.startWorkflow(sfnClient, stateMachineArn, executionJson);
        System.out.println("The ARN of the state machine execution is "+runArn);
        List<String> myList ;
        while (!action) {
            myList = StepFunctionsScenario.getActivityTask(sfnClient, activityArn);
            System.out.println("ChatSFN: " + myList.get(1));
            System.out.println(userName + " please specify a value.");
            String myAction = "done";
            action = true;

            System.out.println("You have selected " + myAction);
            String taskJson = "{ \"action\" : \"" + myAction + "\" }";
            System.out.println(taskJson);
            StepFunctionsScenario.sendTaskSuccess(sfnClient, myList.get(0), taskJson);
        }
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Describe the execution.");
        StepFunctionsScenario.describeExe(sfnClient, runArn);
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Delete the activity.");
        StepFunctionsScenario.deleteActivity(sfnClient, activityArn);
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println(StepFunctionsScenario.DASHES);
        System.out.println("Delete the state machines.");
        StepFunctionsScenario.deleteMachine(sfnClient, stateMachineArn);
        System.out.println(StepFunctionsScenario.DASHES);

        System.out.println("Test 4 passed");
    }

    private  static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/stepfunctions";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/stepfunctions (an AWS Secrets Manager secret)")
    class SecretValues {
        private String roleNameSC;
        private String activityNameSC;
        private String stateMachineNameSC;

        public String getRoleNameSC() {
            return roleNameSC;
        }

        public String getActivityNameSC() {
            return activityNameSC;
        }

        public String getStateMachineNameSC() {
            return stateMachineNameSC;
        }
    }
}
