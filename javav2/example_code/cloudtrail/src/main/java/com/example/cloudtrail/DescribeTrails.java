// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeTrails.java demonstrates how to look up information about a trail.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CloudTrail]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudtrail;

//snippet-start:[cloudtrail.java2.describe_trail.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsRequest;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsResponse;
import software.amazon.awssdk.services.cloudtrail.model.Trail;
import java.util.List;
//snippet-end:[cloudtrail.java2.describe_trail.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeTrails {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <trailName>  \n\n" +
            "Where:\n" +
            "    trailName - The name of the trail to describe. \n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String trailName = args[0];
        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClient = CloudTrailClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        describeSpecificTrails(cloudTrailClient, trailName);
        cloudTrailClient.close();
    }

    //snippet-start:[cloudtrail.java2.describe_trail.main]
    public static void describeSpecificTrails( CloudTrailClient cloudTrailClientClient, String trailName) {

        try {
            DescribeTrailsRequest trailsRequest = DescribeTrailsRequest.builder()
                .trailNameList(trailName)
                .build();

            DescribeTrailsResponse response = cloudTrailClientClient.describeTrails(trailsRequest);
            List<Trail> trails = response.trailList();
            for (Trail trail: trails) {
                System.out.println("The ARN of the trail is "+trail.trailARN());
            }

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.describe_trail.main]
}
