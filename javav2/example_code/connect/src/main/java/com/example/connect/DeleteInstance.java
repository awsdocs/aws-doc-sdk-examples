//snippet-sourcedescription:[DeleteInstance.java demonstrates how to delete an Amazon Connect instance.]
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
import software.amazon.awssdk.services.connect.model.DeleteInstanceRequest;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteInstance {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The id of the instance to delete.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0] ;
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        deleteSpecificInstance(connectClient, instanceId);
    }

    // snippet-start:[connect.java2.delete.main]
    public static void deleteSpecificInstance(ConnectClient connectClient, String instanceId) {
        try {
              DeleteInstanceRequest instanceRequest = DeleteInstanceRequest.builder()
                  .instanceId(instanceId)
                  .build();

            connectClient.deleteInstance(instanceRequest);
            System.out.println("Instance was successfully deleted.");

        } catch (ConnectException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.delete.main]
}
