// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudtrail;

// snippet-start:[cloudtrail.java2.delete_trail.main]
// snippet-start:[cloudtrail.java2.delete_trail.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.DeleteTrailRequest;
// snippet-end:[cloudtrail.java2.delete_trail.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteTrail {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <trailName> \s

                Where:
                    trailName - The name of the trail to delete.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String trailName = args[0];
        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClient = CloudTrailClient.builder()
                .region(region)
                .build();

        deleteSpecificTrail(cloudTrailClient, trailName);
        cloudTrailClient.close();
    }

    public static void deleteSpecificTrail(CloudTrailClient cloudTrailClientClient, String trailName) {
        try {
            DeleteTrailRequest trailRequest = DeleteTrailRequest.builder()
                    .name(trailName)
                    .build();

            cloudTrailClientClient.deleteTrail(trailRequest);
            System.out.println(trailName + " was successfully deleted");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cloudtrail.java2.delete_trail.main]
