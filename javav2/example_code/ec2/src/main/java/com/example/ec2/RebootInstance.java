//snippet-sourcedescription:[RebootInstance.java demonstrates how to reboot an EC2 instance in code.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java2.reboot_instance.complete]
// snippet-start:[ec2.java2.reboot_instance.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RebootInstancesResponse;
 
// snippet-end:[ec2.java2.reboot_instance.import]
/**
 * Reboots and EC2 instance
 */
public class RebootInstance
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply an instance id\n" +
            "Ex: RebootInstance <instance_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instance_id = args[0];
        // snippet-start:[ec2.java2.reboot_instance.main]

        Ec2Client ec2 = Ec2Client.create();

        RebootInstancesRequest request = RebootInstancesRequest.builder()
            .instanceIds(instance_id).build();

        RebootInstancesResponse response = ec2.rebootInstances(request);

        // snippet-end:[ec2.java2.reboot_instance.main]
        System.out.printf(
            "Successfully rebooted instance %s", instance_id);
    }
}
 
// snippet-end:[ec2.java2.reboot_instance.complete]
