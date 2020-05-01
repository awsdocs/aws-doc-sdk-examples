//snippet-sourcedescription:[RebootInstance.java demonstrates how to reboot an Amazon EC2 instance in code.]
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

// snippet-start:[ec2.java2.reboot_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;

// snippet-end:[ec2.java2.reboot_instance.import]
/**
 * Reboots and EC2 instance
 */
public class RebootInstance {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply an instance id\n" +
                        "Ex: RebootInstance <instance_id>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String instanceId = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        rebootEC2Instance(ec2, instanceId);
    }

    // snippet-start:[ec2.java2.reboot_instance.main]
    public static void rebootEC2Instance(Ec2Client ec2, String instanceId) {

      try {

            RebootInstancesRequest request = RebootInstancesRequest.builder()
                .instanceIds(instanceId).build();

            ec2.rebootInstances(request);
            System.out.printf(
                "Successfully rebooted instance %s", instanceId);
    } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
      }
  }
    // snippet-end:[ec2.java2.reboot_instance.main]
}


