// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateTrail.java demonstrates how to create a trail.]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Java]
// snippet-keyword:[AWS CloudTrail]
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

//snippet-start:[cloudtrail.java2.create_trail.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.CreateTrailRequest;
import software.amazon.awssdk.services.cloudtrail.model.CreateTrailResponse;
//snippet-end:[cloudtrail.java2.create_trail.import]

public class CreateTrail {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the trail and an Amazon S3 bucket name.  \n" +
                "\n" +
                "Example: GetTrailStatus <trailName><s3BucketName>\n";

         if (args.length < 2) {
             System.out.println(USAGE);
             System.exit(1);
         }

        /* Read the name from command args */
        String trailName = args[0] ;
        String s3BucketName = args[1] ;

        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClientClient = CloudTrailClient.builder()
                .region(region)
                .build();

        createNewTrail(cloudTrailClientClient, trailName, s3BucketName);
    }

    //snippet-start:[cloudtrail.java2.create_trail.main]
    public static void createNewTrail(CloudTrailClient cloudTrailClientClient, String trailName, String s3BucketName) {

        try {
            CreateTrailRequest trailRequest = CreateTrailRequest.builder()
                .name(trailName)
                .s3BucketName(s3BucketName)
                .isMultiRegionTrail(true)
                .build();

            CreateTrailResponse trailResponse = cloudTrailClientClient.createTrail(trailRequest);
            System.out.println("The Trail ARN is "+trailResponse.trailARN());

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.create_trail.main]
}
