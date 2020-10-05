// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DescribeTrails.java demonstrates how to look up information about a trail.]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CloudTrai]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-15]
// snippet-sourceauthor:[AWS - scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.example.cloudtrail;

//snippet-start:[cloudtrail.java2.describe_trail.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsRequest;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsResponse;
import software.amazon.awssdk.services.cloudtrail.model.Trail;
import java.util.List;
//snippet-end:[cloudtrail.java2.describe_trail.import]

public class DescribeTrails {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the trail.  \n" +
                "\n" +
                "Example: DescribeTrails <trailName>\n";

         if (args.length < 1) {
             System.out.println(USAGE);
             System.exit(1);
         }

        /* Read the name from command args */
        String trailName = args[0];

        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClientClient = CloudTrailClient.builder()
                .region(region)
                .build();

        describeSpecificTrails(cloudTrailClientClient, trailName);
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
