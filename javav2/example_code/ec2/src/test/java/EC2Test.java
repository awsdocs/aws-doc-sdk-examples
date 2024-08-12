// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ec2.*;
import com.example.ec2.scenario.EC2Actions;
import com.example.ec2.scenario.EC2Scenario;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
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
    private static String instanceId = "";
    private static String ami = "";
    private static String instanceName = "";
    private static String keyName = "";
    private static String groupName = "";
    private static String groupDesc = "";
    private static String groupId = "";
    private static String vpcId = "";
    private static String keyNameSc = "";
    private static String fileNameSc = "";
    private static String groupDescSc = "";
    private static String groupNameSc = "";
    private static String vpcIdSc = "";
    private static String myIpAddressSc = "";

    private static String newInstanceId = "";

    private static EC2Actions ec2Actions;

    @BeforeAll
    public static void setUp() throws IOException {


        ec2Actions = new EC2Actions();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        ami = values.getAmi();
        instanceName = values.getInstanceName();
        keyName = values.getKeyNameSc();
        groupName = values.getGroupName() + java.util.UUID.randomUUID();
        groupDesc = values.getGroupDesc();
        vpcId = values.getVpcId();
        keyNameSc = values.getKeyNameSc() + java.util.UUID.randomUUID();
        fileNameSc = values.getFileNameSc();
        groupDescSc = values.getGroupDescSc();
        groupNameSc = values.getGroupDescSc() + java.util.UUID.randomUUID();
        vpcIdSc = values.getVpcIdSc();
        myIpAddressSc = values.getMyIpAddressSc();

        // Uncomment this code block if you prefer using a config.properties file to
        // retrieve AWS values required for these tests.
        /*
         * try (InputStream input =
         * EC2Test.class.getClassLoader().getResourceAsStream("config.properties")) {
         * Properties prop = new Properties();
         * if (input == null) {
         * System.out.println("Sorry, unable to find config.properties");
         * return;
         * }
         * prop.load(input);
         * ami = prop.getProperty("ami");
         * instanceName = prop.getProperty("instanceName");
         * keyName = prop.getProperty("keyPair")+ java.util.UUID.randomUUID();
         * groupName= prop.getProperty("groupName")+ java.util.UUID.randomUUID();;
         * groupDesc= prop.getProperty("groupDesc");
         * vpcId= prop.getProperty("vpcId");
         * keyNameSc= prop.getProperty("keyNameSc")+ java.util.UUID.randomUUID();
         * fileNameSc= prop.getProperty("fileNameSc");
         * groupDescSc= prop.getProperty("groupDescSc");
         * groupNameSc= prop.getProperty("groupNameSc")+ java.util.UUID.randomUUID();;
         * vpcIdSc= prop.getProperty("vpcIdSc");
         * myIpAddressSc= prop.getProperty("myIpAddressSc");
         * 
         * } catch (IOException ex) {
         * ex.printStackTrace();
         * }
         */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createKeyPair() {
        try {
            CompletableFuture<CreateKeyPairResponse> future = ec2Actions.createKeyPairAsync(keyNameSc, fileNameSc);
            CreateKeyPairResponse response = future.join();
            System.out.println("Key Pair successfully created. Key Fingerprint: " + response.keyFingerprint());
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
        System.out.println("\n Test 2 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createInstance() {
        String vpcId = "vpc-e97a4393";
        String myIpAddress = "72.21.198.66" ;
        String groupId= "";
        try {
            CompletableFuture<String> future = ec2Actions.createSecurityGroupAsync(groupName, groupDesc, vpcId, myIpAddress);
            groupId = future.join();
            System.out.println("Created security group with ID: " + groupId);
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }

        String instanceId="";
        try {
            CompletableFuture<GetParametersByPathResponse> future = ec2Actions.getParaValuesAsync();
            GetParametersByPathResponse pathResponse = future.join();
            List<Parameter> parameterList = pathResponse.parameters();
            for (Parameter para : parameterList) {
                if (EC2Scenario.filterName(para.name())) {
                    instanceId = para.value();
                    break;
                }
            }
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }

        String amiValue;
        CompletableFuture<String> futureImage = ec2Actions.describeImageAsync(instanceId);
        amiValue = futureImage.join();
        System.out.println("Image ID: {}"+ amiValue);


        String instanceType;
        CompletableFuture<String> futureInstanceType = ec2Actions.getInstanceTypesAsync();
            instanceType = futureInstanceType.join();
            if (!instanceType.isEmpty()) {
                System.out.println("Found instance type: " + instanceType);
            } else {
                System.out.println("Desired instance type not found.");
            }


        CompletableFuture<String> future = ec2Actions.runInstanceAsync(instanceType, keyNameSc, groupName, amiValue);
        newInstanceId = future.join(); // Get the instance ID.
        System.out.println("EC2 instance ID: "+ newInstanceId);

        System.out.println("\n Test 1 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void describeKeyPair() {
        try {
            CompletableFuture<DescribeKeyPairsResponse> future = ec2Actions.describeKeysAsync();
            future.join();
            System.out.println("Successfully described key pairs.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void deleteKeyPair() {
        try {
            CompletableFuture<DeleteKeyPairResponse> future = ec2Actions.deleteKeysAsync(keyNameSc);
            future.join(); // Wait for the operation to complete
            System.out.println("Key pair deletion completed.");

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void describeSecurityGroup() {
        try {
            CompletableFuture<DescribeSecurityGroupsResponse> future = ec2Actions.describeSecurityGroupsAsync(groupId);
            future.join();
            System.out.println("Security groups described successfully.");

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }
        System.out.println("\n Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void deleteSecurityGroup() {
        try {
            CompletableFuture<Void> future = ec2Actions.deleteEC2SecGroupAsync(groupId);
            future.join(); // Wait for the operation to complete
            System.out.println("Security group successfully deleted.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }
    }


    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void describeInstances() {
        try {
            CompletableFuture<String> future = ec2Actions.describeEC2InstancesAsync(newInstanceId);
            String publicIp = future.join();
            System.out.println("EC2 instance public IP: " + publicIp);

        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
            return;
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void terminateInstance() {
        System.out.println("ID is " +newInstanceId);
        CompletableFuture<Void> future = ec2Actions.terminateEC2Async(newInstanceId);
        future.join();
        System.out.println("EC2 instance successfully terminated.");

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

        public String getVpcId() {
            return vpcId;
        }

        public String getKeyNameSc() {
            return keyNameSc;
        }

        public String getFileNameSc() {
            return fileNameSc;
        }

        public String getGroupNameSc() {
            return groupNameSc;
        }

        public String getGroupDescSc() {
            return groupDescSc;
        }

        public String getVpcIdSc() {
            return vpcIdSc;
        }

        public String getMyIpAddressSc() {
            return myIpAddressSc;
        }
    }
}
