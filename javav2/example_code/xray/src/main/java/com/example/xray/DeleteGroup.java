// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.xray;

// snippet-start:[xray.java2_delete_group.main]
// snippet-start:[xray.java2_delete_group.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.DeleteGroupRequest;
import software.amazon.awssdk.services.xray.model.XRayException;
// snippet-end:[xray.java2_delete_group.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteGroup {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <groupName>

                Where:
                   groupName - The name of the group to delete\s

                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String groupName = args[0];
        Region region = Region.US_EAST_1;
        XRayClient xRayClient = XRayClient.builder()
                .region(region)
                .build();

        deleteSpecificGroup(xRayClient, groupName);
    }

    public static void deleteSpecificGroup(XRayClient xRayClient, String groupName) {
        try {
            DeleteGroupRequest groupRequest = DeleteGroupRequest.builder()
                    .groupName(groupName)
                    .build();

            xRayClient.deleteGroup(groupRequest);
            System.out.println(groupName + " was deleted!");

        } catch (XRayException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[xray.java2_delete_group.main]
