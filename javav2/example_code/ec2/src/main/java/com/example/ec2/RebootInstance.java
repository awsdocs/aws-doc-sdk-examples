//snippet-sourcedescription:[RebootInstance.java demonstrates how to reboot an Amazon Elastic Compute Cloud (Amazon EC2) instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.ec2;

// snippet-start:[ec2.java2.reboot_instance.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
// snippet-end:[ec2.java2.reboot_instance.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RebootInstance {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "   <instanceId> \n\n" +
                "Where:\n" +
                "   instanceId - An instance id value that you can obtain from the AWS Console. \n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        rebootEC2Instance(ec2, instanceId);
        ec2.close();
    }

    // snippet-start:[ec2.java2.reboot_instance.main]
    public static void rebootEC2Instance(Ec2Client ec2, String instanceId) {

      try {
            RebootInstancesRequest request = RebootInstancesRequest.builder()
                .instanceIds(instanceId)
                    .build();

            ec2.rebootInstances(request);
            System.out.printf("Successfully rebooted instance %s", instanceId);

    } catch (Ec2Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
          System.exit(1);
     }
  }
    // snippet-end:[ec2.java2.reboot_instance.main]
}


