/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.ec2.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.services.ec2.Ec2Client;
import java.io.*;
import java.util.*;
import  software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSEC2ServiceIntegrationTest {

    private static  Ec2Client ec2;

    // Define the data members required for the tests
    private static String instanceId = ""; // gets set in test 2
    private static String ami="";
    private static String instanceName="";
    private static String keyName="";
    private static String groupName="";
    private static String groupDesc="";
    private static String groupId="";
    private static String vpcId="";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AWSEC2ServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            ami = prop.getProperty("ami");
            instanceName = prop.getProperty("instanceName");
            keyName = prop.getProperty("keyPair");
            groupName= prop.getProperty("groupName");
            groupDesc= prop.getProperty("groupDesc");
            vpcId= prop.getProperty("vpcId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(ec2);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateInstance() {

        instanceId = CreateInstance.createEC2Instance(ec2,instanceName,ami);
        assertTrue(!instanceId.isEmpty());
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateKeyPair()
    {
        CreateKeyPair.createEC2KeyPair(ec2, keyName);
        System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeKeyPair() {

      DescribeKeyPairs.describeEC2Keys(ec2);
      System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void DeleteKeyPair() {

         DeleteKeyPair.deleteKeys(ec2,keyName);
         System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void CreateSecurityGroup() {

        groupId = CreateSecurityGroup.createEC2SecurityGroup(ec2,groupName,groupDesc,vpcId);
       System.out.println("\n Test 6 passed");
   }

    @Test
    @Order(7)
    public void DescribeSecurityGroup() {

       DescribeSecurityGroups.describeEC2SecurityGroups(ec2,groupId);
       System.out.println("\n Test 7 passed");
    }


    @Test
    @Order(8)
    public void DeleteSecurityGroup(){

      DeleteSecurityGroup.deleteEC2SecGroup(ec2, groupId);
      System.out.println("\n Test 8 passed");
    }


    @Test
    @Order(9)
    public void DescribeAccount() {

     DescribeAccount.describeEC2Account(ec2);
     System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void DescribeInstances() {

       DescribeInstances.describeEC2Instances(ec2);
       System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void DescribeRegionsAndZones () {

      DescribeRegionsAndZones.describeEC2RegionsAndZones(ec2);
      System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
    public void DescribeVPCs () {

      DescribeVPCs.describeEC2Vpcs(ec2,vpcId);
      System.out.println("\n Test 12 passed");
    }

    @Test
    @Order(13)
   public void FindRunningInstances() {
       FindRunningInstances.findRunningEC2Instances(ec2);
       System.out.println("\n Test 13 passed");
    }

    @Test
    @Order(14)
    public void DescribeAddressed() {

       DescribeAddresses.describeEC2Address(ec2);
        System.out.println("\n Test 14 passed");
    }

    @Test
    @Order(15)
   public void  TerminateInstance() {

       TerminateInstance.terminateEC2(ec2, instanceId);
       System.out.println("\n Test 15 passed");
    }
}
