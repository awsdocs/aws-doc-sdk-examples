//snippet-sourcedescription:[DescribeInstance.java demonstrates how to describe the specified instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.DescribeInstanceRequest;
import software.amazon.awssdk.services.connect.model.DescribeInstanceResponse;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeInstance {
    public static void main(String[] args) throws InterruptedException {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The id of the instance to describe.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0] ;
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        describeSpecificInstance(connectClient, instanceId);
    }

    // snippet-start:[connect.java2.describe.instance.main]
    public static void describeSpecificInstance(ConnectClient connectClient, String instanceId) throws InterruptedException {
        boolean status = false;
        try {
            DescribeInstanceRequest instanceRequest = DescribeInstanceRequest.builder()
                .instanceId(instanceId)
                .build();

           while (!status) {
               DescribeInstanceResponse response = connectClient.describeInstance(instanceRequest);
               String instanceStatus = response.instance().instanceStatus().toString();
               System.out.println("Status is " + instanceStatus);
               if (instanceStatus.compareTo("ACTIVE") == 0)
                   status = true;
               Thread.sleep(1000);
           }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.describe.instance.main]
}