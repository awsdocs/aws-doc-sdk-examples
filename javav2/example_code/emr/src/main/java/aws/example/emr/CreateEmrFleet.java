//snippet-sourcedescription:[CreateEmrFleet.java demonstrates how to create a cluster using instance fleet with spot instances]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-keyword:[Spot Instances]
//snippet-service:[emr]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-21]
//snippet-sourceauthor:[valdesis AWS]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */ 
package aws.example.emr;


import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.emr.EmrClient;
import software.amazon.awssdk.services.emr.model.*;

import java.util.Arrays;

/**
 * CreateEmrFleet
 * -------------------------------------
 * 
 */
public class CreateEmrFleet {

    public static void main(String[] args) throws Exception {

        // Instance Types
        // M Family
        InstanceTypeConfig m3xLarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("m3.xlarge")
                                                 .weightedCapacity(1)
                                                 .build();
        InstanceTypeConfig m4xLarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("m4.xlarge")
                                                 .weightedCapacity(1)
                                                 .build();
        InstanceTypeConfig m5xLarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("m5.xlarge")
                                                 .weightedCapacity(1)
                                                 .build();
        // R Family
        InstanceTypeConfig r5xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("r5.xlarge")
                                                 .weightedCapacity(2)
                                                 .build();
        InstanceTypeConfig r4xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("r4.xlarge")
                                                 .weightedCapacity(2)
                                                 .build();
        InstanceTypeConfig r3xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("r3.xlarge")
                                                 .weightedCapacity(2)
                                                 .build();
        // C Family
        InstanceTypeConfig c32xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("c3.2xlarge")
                                                 .weightedCapacity(4)
                                                 .build();
        InstanceTypeConfig c42xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("c4.2xlarge")
                                                 .weightedCapacity(4)
                                                 .build();
        InstanceTypeConfig c52xlarge =  InstanceTypeConfig.builder()
                                                 .bidPriceAsPercentageOfOnDemandPrice(100.0)
                                                 .instanceType("c5.2xlarge")
                                                 .weightedCapacity(4)
                                                 .build();

        // Master
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
        // Core
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
        // Task
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
                                                      .ec2KeyName(System.getenv("EC2_KEY_NAME"))
                                                      .keepJobFlowAliveWhenNoSteps(true)
                                                      .instanceFleets(Arrays.asList(
                                                        masterFleet,
                                                        coreFleet,
                                                        taskFleet
                                                      ))
                                                      .ec2SubnetIds(
                                                        System.getenv("EC2_SUBNETS_IDs").split(",")
                                                      )
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

        EmrClient emr = EmrClient.builder().build();
        RunJobFlowResponse response = emr.runJobFlow(flowRequest);
        System.out.println(response.toString());
        
    }
}
