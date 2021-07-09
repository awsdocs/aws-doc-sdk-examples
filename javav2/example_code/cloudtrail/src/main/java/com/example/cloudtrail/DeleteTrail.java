// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DeleteTrail.java demonstrates how to delete a trail.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS CloudTrail]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/03/2020]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudtrail;

//snippet-start:[cloudtrail.java2.delete_trail.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.DeleteTrailRequest;
//snippet-end:[cloudtrail.java2.delete_trail.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteTrail {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteTrail <trailName>  \n\n" +
                "Where:\n" +
                "    trailName - the name of the trail to delete. \n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
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

    //snippet-start:[cloudtrail.java2.delete_trail.main]
    public static void deleteSpecificTrail(CloudTrailClient cloudTrailClientClient, String trailName){

        try {
            DeleteTrailRequest trailRequest = DeleteTrailRequest.builder()
                .name(trailName)
                .build();

            cloudTrailClientClient.deleteTrail(trailRequest);
            System.out.println(trailName +" was successfully deleted");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.delete_trail.main]
}
