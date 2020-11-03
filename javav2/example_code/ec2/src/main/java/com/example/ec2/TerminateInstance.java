//snippet-sourcedescription:[TerminateInstance.java demonstrates how to terminate an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/01/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.ec2;

// snippet-start:[ec2.java2.terminate_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import java.util.List;
// snippet-end:[ec2.java2.terminate_instance.import]

public class TerminateInstance {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "TerminateInstance <instanceId>\n\n" +
                "Where:\n" +
                "    instanceId - an instance id value that you can obtain from the AWS Console. \n\n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line argument
        String instanceId = args[0];

        // Create an Ec2Client object
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        terminateEC2(ec2, instanceId) ;
        ec2.close();
    }

    // snippet-start:[ec2.java2.terminate_instance]
    public static void terminateEC2( Ec2Client ec2, String instanceID) {

            try{
                TerminateInstancesRequest ti = TerminateInstancesRequest.builder()
                    .instanceIds(instanceID)
                    .build();

                TerminateInstancesResponse response = ec2.terminateInstances(ti);

                List<InstanceStateChange> list = response.terminatingInstances();

                for (int i = 0; i < list.size(); i++) {
                    InstanceStateChange sc = (list.get(i));
                    System.out.println("The ID of the terminated instance is "+sc.instanceId());
                }
            } catch (Ec2Exception e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
         }
    // snippet-end:[ec2.java2.terminate_instance]
    }