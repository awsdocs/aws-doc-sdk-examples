//snippet-sourcedescription:[MonitorInstance.java demonstrates how to toggle detailed monitoring for an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
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

// snippet-start:[ec2.java2.monitor_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.MonitorInstancesRequest;
import software.amazon.awssdk.services.ec2.model.UnmonitorInstancesRequest;
// snippet-end:[ec2.java2.monitor_instance.import]

/**
 * Toggles detailed monitoring for an EC2 instance
 */
public class MonitorInstance {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "MonitorInstance <instanceId> <monitor>\n\n" +
                "Where:\n" +
                "    instanceId - an instance id value that you can obtain from the AWS Console. \n\n" +
                "    monitor - a monitoring status (true|false)";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line arguments
        String instanceId = args[0];
        boolean monitor = Boolean.valueOf(args[1]);

        // Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        if (monitor) {
            monitorInstance(ec2, instanceId);
        } else {
            unmonitorInstance(ec2, instanceId);
        }
        ec2.close();
    }


    public static void monitorInstance( Ec2Client ec2, String instanceId) {
        // snippet-start:[ec2.java2.monitor_instance.main]
        MonitorInstancesRequest request = MonitorInstancesRequest.builder()
                .instanceIds(instanceId).build();

        ec2.monitorInstances(request);

        // snippet-end:[ec2.java2.monitor_instance.main]
        System.out.printf(
                "Successfully enabled monitoring for instance %s",
                instanceId);
    }

    public static void unmonitorInstance( Ec2Client ec2, String instanceId) {
        // snippet-start:[ec2.java2.monitor_instance.stop]
        UnmonitorInstancesRequest request = UnmonitorInstancesRequest.builder()
                .instanceIds(instanceId).build();

        ec2.unmonitorInstances(request);
        // snippet-end:[ec2.java2.monitor_instance.stop]
        System.out.printf(
                "Successfully disabled monitoring for instance %s",
                instanceId);
    }

}