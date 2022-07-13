//snippet-sourcedescription:[CreateEmrFleet.java demonstrates how to create a cluster using instance fleet with spot instances.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package aws.example.emr;

// snippet-start:[emr.java2._create_fleet.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.InstanceTypeConfig;
import software.amazon.awssdk.services.emr.model.InstanceFleetConfig;
import software.amazon.awssdk.services.emr.model.InstanceFleetType;
import software.amazon.awssdk.services.emr.model.JobFlowInstancesConfig;
import software.amazon.awssdk.services.emr.model.RunJobFlowRequest;
import software.amazon.awssdk.services.emr.model.Application;
import software.amazon.awssdk.services.emr.model.RunJobFlowResponse;
import software.amazon.awssdk.services.emr.model.EmrException;
import java.util.Arrays;
// snippet-end:[emr.java2._create_fleet.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *  Also. to run the CreateEmrFleet example, it is recommended that you go through the following document:
 *
 * https://docs.aws.amazon.com/emr/latest/ManagementGuide/emr-gs.html
 * -------------------------------------
 */
public class CreateEmrFleet {

    public static void main(String[] args) throws Exception {

        Region region = Region.US_EAST_1;
        EmrClient emrClient = EmrClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        createFleet(emrClient);
    }

// snippet-start:[emr.java2._create_fleet.main]
public static void createFleet(EmrClient emr) {

   try {
        InstanceTypeConfig m3xLarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("m3.xlarge")
                .weightedCapacity(1)
                .build();

        InstanceTypeConfig m4xLarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("m4.xlarge")
                .weightedCapacity(1)
                .build();

        InstanceTypeConfig m5xLarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("m5.xlarge")
                .weightedCapacity(1)
                .build();

        InstanceTypeConfig r5xlarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("r5.xlarge")
                .weightedCapacity(2)
                .build();

        InstanceTypeConfig r4xlarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("r4.xlarge")
                .weightedCapacity(2)
                .build();

        InstanceTypeConfig r3xlarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("r3.xlarge")
                .weightedCapacity(2)
                .build();

        InstanceTypeConfig c32xlarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("c3.2xlarge")
                .weightedCapacity(4)
                .build();

        InstanceTypeConfig c42xlarge = InstanceTypeConfig.builder()
                .bidPriceAsPercentageOfOnDemandPrice(100.0)
                .instanceType("c4.2xlarge")
                .weightedCapacity(4)
                .build();

        InstanceFleetConfig masterFleet = InstanceFleetConfig.builder()
                .name("master-fleet")
                .instanceFleetType(InstanceFleetType.MASTER)
                .instanceTypeConfigs(Arrays.asList(
                        m3xLarge,
                        m4xLarge,
                        m5xLarge
                ))
                .targetOnDemandCapacity(1)
                .build();

        InstanceFleetConfig coreFleet = InstanceFleetConfig.builder()
                .name("core-fleet")
                .instanceFleetType(InstanceFleetType.CORE)
                .instanceTypeConfigs(Arrays.asList(
                        m3xLarge,
                        m4xLarge,
                        r4xlarge,
                        r3xlarge,
                        c32xlarge
                ))
                .targetOnDemandCapacity(20)
                .targetSpotCapacity(10)
                .build();

        InstanceFleetConfig taskFleet = InstanceFleetConfig.builder()
                .name("task-fleet")
                .instanceFleetType(InstanceFleetType.TASK)
                .instanceTypeConfigs(Arrays.asList(
                        m4xLarge,
                        r5xlarge,
                        r4xlarge,
                        c32xlarge,
                        c42xlarge
                ))
                .targetOnDemandCapacity(8)
                .targetSpotCapacity(40)
                .build();

        JobFlowInstancesConfig flowInstancesConfig = JobFlowInstancesConfig.builder()
                .ec2KeyName(System.getenv("keypair1"))
                .keepJobFlowAliveWhenNoSteps(true)
                .instanceFleets(Arrays.asList(
                        masterFleet,
                        coreFleet,
                        taskFleet
                ))
                .ec2SubnetId("subnet-cca64baa")
                .build();

        RunJobFlowRequest flowRequest = RunJobFlowRequest.builder()
                .name("emr-spot-example")
                .instances(flowInstancesConfig)
                .serviceRole("EMR_DefaultRole")
                .jobFlowRole("EMR_EC2_DefaultRole")
                .visibleToAllUsers(true)
                .applications(java.util.Arrays.asList(
                        Application.builder().name("Spark").build()
                ))
                .releaseLabel("emr-5.29.0")
                .build();

         RunJobFlowResponse response = emr.runJobFlow(flowRequest);
         System.out.println(response.toString());

    } catch (EmrException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
  }
    // snippet-end:[emr.java2._create_fleet.main]
}