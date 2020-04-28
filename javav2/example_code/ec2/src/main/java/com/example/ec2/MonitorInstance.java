//snippet-sourcedescription:[MonitorInstance.java demonstrates how to toggle detailed monitoring for an Amazon EC2 instance.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/12/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
        final String USAGE =
                "To run this example, supply an instance id and a monitoring " +
                        "status\n" +
                        "Ex: MonitorInstance <instance-id> <true|false>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];
        boolean monitor = Boolean.valueOf(args[1]);

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        if (monitor) {
            monitorInstance(ec2, instanceId);
        } else {
            unmonitorInstance(ec2, instanceId);
        }
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
