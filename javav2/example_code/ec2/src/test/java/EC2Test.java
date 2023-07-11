/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.ec2.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.ec2.Ec2Client;
import java.io.*;
import java.util.concurrent.TimeUnit;

import  software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EC2Test {

    private static  Ec2Client ec2;
    private static SsmClient ssmClient;

    // Define the data members required for the tests.
    private static String instanceId = ""; // gets set in test 2.
    private static String ami="";
    private static String instanceName="";
    private static String keyName="";
    private static String groupName="";
    private static String groupDesc="";
    private static String groupId="";
    private static String vpcId="";
    private static String keyNameSc="";
    private static String fileNameSc="";
    private static String groupDescSc="";
    private static String groupNameSc="";
    private static String vpcIdSc="";
    private static String myIpAddressSc="";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_WEST_2;
        ec2 = Ec2Client.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        ami = values.getAmi();
        instanceName = values.getInstanceName();
        keyName = values.getKeyNameSc();
        groupName= values.getGroupName();
        groupDesc= values.getGroupDesc();
        vpcId= values.getVpcId();
        keyNameSc= values.getKeyNameSc() + java.util.UUID.randomUUID();
        fileNameSc= values.getFileNameSc();
        groupDescSc= values.getGroupDescSc();
        groupNameSc= values.getGroupDescSc()+ java.util.UUID.randomUUID();
        vpcIdSc= values.getVpcIdSc();
        myIpAddressSc= values.getMyIpAddressSc();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = EC2Test.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            ami = prop.getProperty("ami");
            instanceName = prop.getProperty("instanceName");
            keyName = prop.getProperty("keyPair")+ java.util.UUID.randomUUID();
            groupName= prop.getProperty("groupName")+ java.util.UUID.randomUUID();;
            groupDesc= prop.getProperty("groupDesc");
            vpcId= prop.getProperty("vpcId");
            keyNameSc= prop.getProperty("keyNameSc")+ java.util.UUID.randomUUID();
            fileNameSc= prop.getProperty("fileNameSc");
            groupDescSc= prop.getProperty("groupDescSc");
            groupNameSc= prop.getProperty("groupNameSc")+ java.util.UUID.randomUUID();;
            vpcIdSc= prop.getProperty("vpcIdSc");
            myIpAddressSc= prop.getProperty("myIpAddressSc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
     */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateInstance() {
        instanceId = CreateInstance.createEC2Instance(ec2,instanceName,ami);
        assertFalse(instanceId.isEmpty());
        System.out.println("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateKeyPair() {
        assertDoesNotThrow(() ->CreateKeyPair.createEC2KeyPair(ec2, keyName));
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeKeyPair() {
        assertDoesNotThrow(() ->DescribeKeyPairs.describeEC2Keys(ec2));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DeleteKeyPair() {
        assertDoesNotThrow(() -> DeleteKeyPair.deleteKeys(ec2,keyName));
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void CreateSecurityGroup() {
        groupId = CreateSecurityGroup.createEC2SecurityGroup(ec2,groupName,groupDesc,vpcId);
        assertFalse(groupId.isEmpty());
        System.out.println("\n Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void DescribeSecurityGroup() {
        assertDoesNotThrow(() -> DescribeSecurityGroups.describeEC2SecurityGroups(ec2,groupId));
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteSecurityGroup(){
        assertDoesNotThrow(() -> DeleteSecurityGroup.deleteEC2SecGroup(ec2, groupId));
        System.out.println("\n Test 7 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void DescribeAccount() {
        assertDoesNotThrow(() -> DescribeAccount.describeEC2Account(ec2));
        System.out.println("\n Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void DescribeInstances() {
        assertDoesNotThrow(() -> DescribeInstances.describeEC2Instances(ec2));
        System.out.println("\n Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void DescribeRegionsAndZones () {
        assertDoesNotThrow(() -> DescribeRegionsAndZones.describeEC2RegionsAndZones(ec2));
        System.out.println("\n Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void DescribeVPCs () {
        assertDoesNotThrow(() -> DescribeVPCs.describeEC2Vpcs(ec2,vpcId));
        System.out.println("\n Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
   public void FindRunningInstances() {
        assertDoesNotThrow(() -> FindRunningInstances.findRunningEC2Instances(ec2));
        System.out.println("\n Test 12 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void DescribeAddressed() {
        assertDoesNotThrow(() -> DescribeAddresses.describeEC2Address(ec2));
        System.out.println("\n Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
   public void  TerminateInstance() {
       assertDoesNotThrow(() -> TerminateInstance.terminateEC2(ec2, instanceId));
       System.out.println("\n Test 14 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void  TestEC2Scenario() throws InterruptedException {
        System.out.println(EC2Scenario.DASHES);
        System.out.println("1. Create an RSA key pair and save the private key material as a .pem file.");
        EC2Scenario.createKeyPair(ec2, keyNameSc, fileNameSc);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("2. List key pairs.");
        EC2Scenario.describeKeys(ec2);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("3. Create a security group.");
        String groupId = EC2Scenario.createSecurityGroup(ec2, groupNameSc, groupDescSc, vpcIdSc,myIpAddressSc);
        assertFalse(groupId.isEmpty());
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("4. Display security group info for the newly created security group.");
        EC2Scenario.describeSecurityGroups(ec2, groupId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("5. Get a list of Amazon Linux 2 AMIs and select one with amzn2 in the name.");
        String instanceId = EC2Scenario.getParaValues(ssmClient);
        assertFalse(instanceId.isEmpty());
        System.out.println("The instance Id is "+instanceId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("6. Get more information about an amzn2 image.");
        String amiValue = EC2Scenario.describeImage(ec2, instanceId);
        assertFalse(amiValue.isEmpty());
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("7. Get a list of instance types.");
        String instanceType = EC2Scenario.getInstanceTypes(ec2);
        assertFalse(instanceType.isEmpty());
        if (instanceType.compareTo("x2gd.metal") != 0)
            instanceType = "x2gd.metal";
        System.out.println("*** The instance type is "+instanceType);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("Wait 1 min before creating the instance using "+ instanceType);
        TimeUnit.MINUTES.sleep(1);
        System.out.println("8. Create an instance.");
        String newInstanceId = EC2Scenario.runInstance(ec2, instanceType, keyNameSc, groupNameSc, amiValue );
        System.out.println("The instance Id is "+newInstanceId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("9. Display information about the running instance. ");
        String ipAddress = EC2Scenario.describeEC2Instances(ec2, newInstanceId);
        assertFalse(ipAddress.isEmpty());
        System.out.println("You can SSH to the instance using this command:");
        System.out.println("ssh -i "+fileNameSc +"ec2-user@"+ipAddress);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("10.  Stop the instance.");
        EC2Scenario.stopInstance(ec2, newInstanceId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("11.  Start the instance.");
        EC2Scenario.startInstance(ec2, newInstanceId);
        ipAddress = EC2Scenario.describeEC2Instances(ec2, newInstanceId);
        assertFalse(ipAddress.isEmpty());
        System.out.println("You can SSH to the instance using this command:");
        System.out.println("ssh -i "+fileNameSc +"ec2-user@"+ipAddress);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("12. Allocate an Elastic IP address and associate it with the instance.");
        String allocationId = EC2Scenario.allocateAddress(ec2);
        assertFalse(allocationId.isEmpty());
        System.out.println("The allocation Id value is "+allocationId);
        String associationId = EC2Scenario.associateAddress(ec2, newInstanceId, allocationId);
        System.out.println("The association Id value is "+associationId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("13. Describe the instance again.");
        ipAddress = EC2Scenario.describeEC2Instances(ec2, newInstanceId);
        assertFalse(ipAddress.isEmpty());
        System.out.println("You can SSH to the instance using this command:");
        System.out.println("ssh -i "+fileNameSc +"ec2-user@"+ipAddress);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("14. Disassociate and release the Elastic IP address.");
        EC2Scenario.disassociateAddress(ec2, associationId);
        EC2Scenario.releaseEC2Address(ec2, allocationId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("15. Terminate the instance.");
        EC2Scenario.terminateEC2(ec2, newInstanceId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("16. Delete the security group.");
        EC2Scenario.deleteEC2SecGroup(ec2, groupId);
        System.out.println(EC2Scenario.DASHES);

        System.out.println(EC2Scenario.DASHES);
        System.out.println("17. Delete the keys.");
        EC2Scenario.deleteKeys(ec2, keyNameSc);
        System.out.println(EC2Scenario.DASHES);
        System.out.println("\n Test 16 passed");
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

