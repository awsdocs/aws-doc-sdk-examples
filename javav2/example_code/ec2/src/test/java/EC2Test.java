// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ec2.scenario.EC2Actions;
import com.example.ec2.scenario.EC2Scenario;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.KeyPairInfo;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EC2Test {

    private static String keyName = "";
    private static String groupName = "";
    private static String groupDesc = "";
    private static String groupId = "";
    private static String keyNameSc = "";
    private static String fileNameSc = "";
    private static String vpcIdSc = "";
    private static String myIpAddressSc = "";

    private static String newInstanceId = "";

    private static EC2Actions ec2Actions;

    @BeforeAll
    public static void setUp() {
        ec2Actions = new EC2Actions();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
         keyName = values.getKeyNameSc();
        groupName = values.getGroupName() + java.util.UUID.randomUUID();
        groupDesc = values.getGroupDesc();
        keyNameSc = values.getKeyNameSc() + java.util.UUID.randomUUID();
        fileNameSc = values.getFileNameSc();
        vpcIdSc = values.getVpcIdSc();
        myIpAddressSc = values.getMyIpAddressSc();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createKeyPair() {
        try {
            CompletableFuture<CreateKeyPairResponse> future = ec2Actions.createKeyPairAsync(keyNameSc, fileNameSc);
            CreateKeyPairResponse response = future.join();

            // Assert that the response is not null.
            Assertions.assertNotNull(response, "The response should not be null");

            // Assert specific properties of the response
            Assertions.assertNotNull(response.keyFingerprint(), "The key fingerprint should not be null");
            Assertions.assertFalse(response.keyFingerprint().isEmpty(), "The key fingerprint should not be empty");
            System.out.println("Key Pair successfully created. Key Fingerprint: " + response.keyFingerprint());

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception: " + rte.getMessage());
        }

        System.out.println("Test 1 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createInstance() {
        try {
            CompletableFuture<String> future = ec2Actions.createSecurityGroupAsync(groupName, groupDesc, vpcIdSc, myIpAddressSc);
            groupId = future.join();

            // Assert that the security group ID is not null or empty
            Assertions.assertNotNull(groupId, "The security group ID should not be null");
            Assertions.assertFalse(groupId.isEmpty(), "The security group ID should not be empty");

            System.out.println("Created security group with ID: " + groupId);
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while creating security group: " + rte.getMessage());
        }

         String instanceId ="";
        try {
            CompletableFuture<GetParametersByPathResponse> future = ec2Actions.getParaValuesAsync();
            GetParametersByPathResponse pathResponse = future.join();

            // Assert that the pathResponse is not null.
            Assertions.assertNotNull(pathResponse, "The response from getParaValuesAsync should not be null");

            List<Parameter> parameterList = pathResponse.parameters();
            Assertions.assertFalse(parameterList.isEmpty(), "Parameter list should not be empty");
            for (Parameter para : parameterList) {
                if (EC2Scenario.filterName(para.name())) {
                    instanceId = para.value();
                    break;
                }
            }

            // Assert that instanceId is found and not empty
            Assertions.assertNotNull(instanceId, "The instance ID should not be null");
            Assertions.assertFalse(instanceId.isEmpty(), "The instance ID should not be empty");

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while retrieving parameters: " + rte.getMessage());
        }


        String amiValue="";
        try {
            CompletableFuture<String> futureImage = ec2Actions.describeImageAsync(instanceId);
            amiValue = futureImage.join();

            // Assert that the AMI value is not null or empty
            Assertions.assertNotNull(amiValue, "The AMI value should not be null");
            Assertions.assertFalse(amiValue.isEmpty(), "The AMI value should not be empty");

            System.out.println("Image ID: " + amiValue);
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while describing image: " + rte.getMessage());
        }

        String instanceType="";
        try {
            CompletableFuture<String> futureInstanceType = ec2Actions.getInstanceTypesAsync();
            instanceType = futureInstanceType.join();

            // Assert that the instance type is not null or empty
            Assertions.assertNotNull(instanceType, "The instance type should not be null");
            Assertions.assertFalse(instanceType.isEmpty(), "The instance type should not be empty");

            System.out.println("Found instance type: " + instanceType);
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while getting instance type: " + rte.getMessage());
        }

        try {
            CompletableFuture<String> future = ec2Actions.runInstanceAsync(instanceType, keyName, groupName, amiValue);
            newInstanceId = future.join(); // Get the instance ID.

            // Assert that the new instance ID is not null or empty
            Assertions.assertNotNull(newInstanceId, "The new instance ID should not be null");
            Assertions.assertFalse(newInstanceId.isEmpty(), "The new instance ID should not be empty");

            System.out.println("EC2 instance ID: " + newInstanceId);
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while running instance: " + rte.getMessage());
        }

        System.out.println("\n Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void describeKeyPair() {
        try {
            CompletableFuture<DescribeKeyPairsResponse> future = ec2Actions.describeKeysAsync();
            DescribeKeyPairsResponse response = future.join();

            // Assert that the response is not null
            Assertions.assertNotNull(response, "The response from describeKeysAsync should not be null");

            // Assert that the key pairs list is not null or empty
            List<KeyPairInfo> keyPairs = response.keyPairs();
            Assertions.assertNotNull(keyPairs, "The key pairs list should not be null");
            Assertions.assertFalse(keyPairs.isEmpty(), "The key pairs list should not be empty");

            // Optionally, you can print out the details of each key pair
            keyPairs.forEach(keyPair ->
                System.out.println("Key Pair Name: " + keyPair.keyName() + ", Key Fingerprint: " + keyPair.keyFingerprint())
            );

            System.out.println("Successfully described key pairs.");

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while describing key pairs: " + rte.getMessage());
        }

        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void deleteKeyPair() {
        try {
            CompletableFuture<DeleteKeyPairResponse> future = ec2Actions.deleteKeysAsync(keyNameSc);
            DeleteKeyPairResponse response = future.join();

            // Assert that the response is not null.
            Assertions.assertNotNull(response, "The response from deleteKeysAsync should not be null");
            System.out.println("Key pair deletion completed.");

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while deleting key pair: " + rte.getMessage());
        }

        System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void describeSecurityGroup() {
        try {
            CompletableFuture<String> future = ec2Actions.describeSecurityGroupArnByNameAsync(groupName);
            groupId = future.join();
            // Assert that the response is not null
            Assertions.assertNotNull(groupId, "The response from describeSecurityGroupsAsync should not be null");


        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while describing security groups: " + rte.getMessage());
        }

        System.out.println("\n Test 5 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void describeInstances() {
        try {
            CompletableFuture<String> future = ec2Actions.describeEC2InstancesAsync(newInstanceId);
            String publicIp = future.join();

            // Assert that the public IP is not null or empty.
            Assertions.assertNotNull(publicIp, "The public IP should not be null");
            Assertions.assertFalse(publicIp.isEmpty(), "The public IP should not be empty");
            System.out.println("EC2 instance public IP: " + publicIp);

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while describing EC2 instances: " + rte.getMessage());
        }
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void terminateInstance() {
        try {
            System.out.println("Instance ID is: " + newInstanceId);
            CompletableFuture<Object> future = ec2Actions.terminateEC2Async(newInstanceId);
            future.join(); // Wait for the operation to complete

            // Since the operation returns void, reaching this point indicates success.
            System.out.println("EC2 instance successfully terminated.");

        } catch (RuntimeException rte) {
            // Handle any runtime exceptions and fail the test if one occurs
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            Assertions.fail("Test failed due to an unexpected exception while terminating the EC2 instance: " + rte.getMessage());
        }

        // Confirm that the test passed
        System.out.println("\n Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/ec2";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ec2 (an AWS Secrets Manager secret)")
    class SecretValues {
        private String ami;
        private String instanceName;
        private String keyPair;

        private String groupName;

        private String groupDesc;
        private String vpcId;

        private String keyNameSc;

        private String fileNameSc;

        private String groupNameSc;

        private String groupDescSc;

        private String vpcIdSc;

        private String myIpAddressSc;

        public String getAmi() {
            return ami;
        }

        public String getInstanceName() {
            return instanceName;
        }

        public String getKeyPair() {
            return keyPair;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupDesc() {
            return groupDesc;
        }

        public String getKeyNameSc() {
            return keyNameSc;
        }

        public String getFileNameSc() {
            return fileNameSc;
        }

        public String getVpcIdSc() {
            return vpcIdSc;
        }

        public String getMyIpAddressSc() {
            return myIpAddressSc;
        }
    }
}
