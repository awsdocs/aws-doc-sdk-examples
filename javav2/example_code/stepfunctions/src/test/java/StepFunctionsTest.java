/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.stepfunctions.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StepFunctionsTest {

    private static  SfnClient sfnClient;
    private static String stateMachineArn = ""; // Gets dynamically set in a test.
    private static String exeArn = "";  // Gets dynamically set in a test.
    private static String jsonFile = "";
    private static String jsonFileSM = "";
    private static String roleARN = "";
    private static String stateMachineName = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        sfnClient = SfnClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = StepFunctionsTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // load the properties file.
            prop.load(input);

            // Populate the data members required for all tests.
            jsonFile = prop.getProperty("jsonFile");
            jsonFileSM = prop.getProperty("jsonFileSM");
            roleARN = prop.getProperty("roleARN");
            stateMachineName = prop.getProperty("stateMachineName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(sfnClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateStateMachine() {
        stateMachineArn = CreateStateMachine.createMachine(sfnClient, roleARN, stateMachineName, jsonFileSM);
        assertTrue(!stateMachineArn.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void StartExecution() {
        exeArn = StartExecution.startWorkflow(sfnClient, stateMachineArn, jsonFile);
        assertTrue(!stateMachineArn.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListStateMachines() {
        ListStateMachines.listMachines(sfnClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListActivities() {
        ListActivities.listAllActivites(sfnClient);
        System.out.println("Test 5 passed");

    }


    @Test
    @Order(6)
    public void GetExecutionHistory() {
        GetExecutionHistory.getExeHistory(sfnClient,exeArn );
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DeleteStateMachine() {

        DeleteStateMachine.deleteMachine(sfnClient, stateMachineArn);
        System.out.println("Test 7 passed");
    }

}
