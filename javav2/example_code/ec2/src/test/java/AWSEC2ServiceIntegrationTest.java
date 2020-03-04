import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;
import java.io.*;
import java.util.*;

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
    private static String vpcId="";

    @BeforeAll
    public static void setUp() throws IOException {

        ec2 = Ec2Client.create();
        try (InputStream input = AWSEC2ServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
           // instanceId = prop.getProperty("instanceId");
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
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(ec2);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateIntance() {

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(ami)
                .instanceType(InstanceType.T1_MICRO)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        instanceId = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("Name")
                .value(instanceName)
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        try {
            ec2.createTags(tagRequest);

            System.out.printf(
                    "Successfully started EC2 instance %s based on AMI %s",
                    instanceId, ami);
        } catch (Ec2Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateKeyPair()
    {

       try {

           CreateKeyPairRequest request = CreateKeyPairRequest.builder()
                .keyName(keyName).build();

           CreateKeyPairResponse response = ec2.createKeyPair(request);

           System.out.printf(
                "Successfully created key pair named %s",
                keyName);

    } catch (Ec2Exception e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
        System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeKeyPair() {

     try {
        //Just describe the speicific key pair just created
        DescribeKeyPairsRequest request = DescribeKeyPairsRequest.builder()
                .keyNames(keyName)
                .build();

        DescribeKeyPairsResponse response = ec2.describeKeyPairs(request);

        for(KeyPairInfo keyPair : response.keyPairs()) {
            System.out.printf(
                    "Found key pair with name %s " +
                            "and fingerprint %s",
                    keyPair.keyName(),
                    keyPair.keyFingerprint());
            System.out.println("");
        }
     } catch (Ec2Exception e) {
         System.err.println(e.awsErrorDetails().errorMessage());
         System.exit(1);
     }
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void DeleteKeyPair() {

        try {
            DeleteKeyPairRequest request = DeleteKeyPairRequest.builder()
                .keyName(keyName)
                .build();

            DeleteKeyPairResponse response = ec2.deleteKeyPair(request);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

       System.out.println("\n Test 5 passed");
    }


    @Test
    @Order(6)
    public void CreateSecurityGroup() {

      try {

          CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
                .groupName(groupName)
                .description(groupDesc)
                .vpcId(vpcId)
                .build();

          CreateSecurityGroupResponse createResponse =
                ec2.createSecurityGroup(createRequest);

          IpRange ipRange = IpRange.builder()
               .cidrIp("0.0.0.0/0").build();

          IpPermission ipPerm = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(80)
                .fromPort(80)
                .ipRanges(ipRange)
                // .ipv4Ranges(ip_range)
                .build();

          IpPermission ipPerm2 = IpPermission.builder()
                .ipProtocol("tcp")
                .toPort(22)
                .fromPort(22)
                .ipRanges(ipRange)
                .build();

          AuthorizeSecurityGroupIngressRequest authRequest =
                AuthorizeSecurityGroupIngressRequest.builder()
                        .groupName(groupName)
                        .ipPermissions(ipPerm, ipPerm2)
                        .build();

           ec2.authorizeSecurityGroupIngress(authRequest);
      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }

      System.out.println("\n Test 6 passed");
   }

    @Test
    @Order(7)
    public void DescribeSecurityGroup() {

      try {
        DescribeSecurityGroupsRequest request =
                DescribeSecurityGroupsRequest.builder()
                        .groupNames(groupName)
                        .build();

        DescribeSecurityGroupsResponse response =
                ec2.describeSecurityGroups(request);

       for(SecurityGroup group : response.securityGroups()) {
            System.out.printf(
                    "Found security group with id %s, " +
                            "vpc id %s " +
                            "and description %s",
                    group.groupId(),
                    group.vpcId(),
                    group.description());
        }
      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
        System.out.println("\n Test 7 passed");
    }


    @Test
    @Order(8)
    public void DeleteSecurityGroup(){

      try {
        DeleteSecurityGroupRequest request = DeleteSecurityGroupRequest.builder()
                .groupName(groupName)
                .build();

        ec2.deleteSecurityGroup(request);

      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }

        System.out.println("\n Test 8 passed");
    }


    @Test
    @Order(9)
    public void DescribeAccount() {

      try{
            DescribeAccountAttributesResponse accountResults = ec2.describeAccountAttributes();

            List<AccountAttribute> accountList = accountResults.accountAttributes();

            for (ListIterator iter = accountList.listIterator(); iter.hasNext(); ) {

                AccountAttribute attribute = (AccountAttribute) iter.next();
                System.out.print("\n The name of the attribute is "+attribute.attributeName());
                List<AccountAttributeValue> values = attribute.attributeValues();

                //iterate through the attribute values
                for (ListIterator iterVals = values.listIterator(); iterVals.hasNext(); ) {
                    AccountAttributeValue myValue = (AccountAttributeValue) iterVals.next();
                    System.out.print("\n The value of the attribute is "+myValue.attributeValue());
                }
            }

      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
        System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void DescribeInstances() {

       try {
        boolean done = false;

        String nextToken = null;
        do {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.printf(
                            "Found reservation with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.instanceId(),
                            instance.imageId(),
                            instance.instanceType(),
                            instance.state().name(),
                            instance.monitoring().state());
                    System.out.println("");
                }
            }
            nextToken = response.nextToken();

        } while (nextToken != null);

       } catch (Ec2Exception e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }

       System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void DescribeRegionsAndZones () {

      try {
        DescribeRegionsResponse regionsResponse = ec2.describeRegions();

        for(Region region : regionsResponse.regions()) {
            System.out.printf(
                    "Found region %s " +
                            "with endpoint %s",
                    region.regionName(),
                    region.endpoint());
            System.out.println();
        }

        DescribeAvailabilityZonesResponse zonesResponse =
                ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zonesResponse.availabilityZones()) {
            System.out.printf(
                    "Found availability zone %s " +
                            "with status %s " +
                            "in region %s",
                    zone.zoneName(),
                    zone.state(),
                    zone.regionName());
            System.out.println();

        }
      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }

        System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
    public void DescribeVPCs () {

      try {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder().vpcIds(vpcId).build();

        DescribeVpcsResponse response =
                ec2.describeVpcs(request);

        for (Vpc vpc : response.vpcs()) {
            System.out.printf(
                    "Found vpc with id %s, " +
                            "vpc state %s " +
                            "and tennancy %s",
                    vpc.vpcId(),
                    vpc.stateAsString(),
                    vpc.instanceTenancyAsString());
        }
      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
        System.out.println("\n Test 12 passed");
    }


    @Test
    @Order(13)
   public void FindRunningInstances() {
      try {
        String nextToken = null;
        do {
            // Create a Filter to find all running instances
            Filter filter = Filter.builder()
                    .name("instance-state-name")
                    .values("running")
                    .build();

            //Create a DescribeInstancesRequest
            DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                    .filters(filter)
                    .build();

            // Find the running instances
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    System.out.printf(
                            "Found reservation with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.instanceId(),
                            instance.imageId(),
                            instance.instanceType(),
                            instance.state().name(),
                            instance.monitoring().state());

                }
            }
            nextToken = response.nextToken();

        } while (nextToken != null);

      } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
        System.out.println("\n Test 13 passed");
    }

    @Test
    @Order(14)
   public void  TerminateInstance() {

       try {
           // Create an Ec2Client object
           TerminateInstancesRequest ti = TerminateInstancesRequest.builder()
                   .instanceIds("i-0ca327cd33788a4f7")
                   .build();

           ec2.terminateInstances(ti);

       } catch (Ec2Exception e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
        System.out.println("\n Test 14 passed");
    }
}
