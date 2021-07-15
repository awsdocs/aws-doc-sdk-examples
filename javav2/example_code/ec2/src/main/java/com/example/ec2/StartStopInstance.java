//snippet-sourcedescription:[StartStopInstance.java demonstrates how to start and stop an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
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

// snippet-start:[ec2.java2.start_stop_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
// snippet-end:[ec2.java2.start_stop_instance.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StartStopInstance {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "StartStopInstance <instanceId>\n\n" +
                "Where:\n" +
                "    instanceId - an instance id value that you can obtain from the AWS Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];
        boolean start;

        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        if(args[1].equals("start")) {
            start = true;
        } else {
            start = false;
        }

        if(start) {
            startInstance(ec2, instanceId);
        } else {
            stopInstance(ec2, instanceId);
        }
        ec2.close();
    }

    // snippet-start:[ec2.java2.start_stop_instance.start]
    public static void startInstance(Ec2Client ec2, String instanceId) {

        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2.startInstances(request);

        // snippet-end:[ec2.java2.start_stop_instance.start]
        System.out.printf("Successfully started instance %s", instanceId);
    }

    // snippet-start:[ec2.java2.start_stop_instance.stop]
    public static void stopInstance(Ec2Client ec2, String instanceId) {

        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2.stopInstances(request);

        // snippet-end:[ec2.java2.start_stop_instance.stop]
        System.out.printf("Successfully stop instance %s", instanceId);
    }
}

