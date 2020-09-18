// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[StartLogging.java demonstrates how to start and stop logging.]
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

//snippet-start:[cloudtrail.java2.logging.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.StartLoggingRequest;
import software.amazon.awssdk.services.cloudtrail.model.StopLoggingRequest;
//snippet-end:[cloudtrail.java2.logging.import]

public class StartLogging {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the trail.  \n" +
                "\n" +
                "Ex: StartLogging <trailName>\n";

         if (args.length < 1) {
             System.out.println(USAGE);
             System.exit(1);
         }

        /* Read the name from command args */
        String trailName = args[0] ;

        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClientClient = CloudTrailClient.builder()
                .region(region)
                .build();

        // Start logging
        startLog(cloudTrailClientClient, trailName);

        // Stop logging
        stopLog(cloudTrailClientClient, trailName);

    }

    //snippet-start:[cloudtrail.java2.logging.main]
    public static void startLog( CloudTrailClient cloudTrailClientClient, String trailName) {

        try {
            StopLoggingRequest loggingRequest = StopLoggingRequest.builder()
                .name(trailName)
                .build() ;

            cloudTrailClientClient.stopLogging(loggingRequest);
            System.out.println(trailName +" has stopped logging");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void stopLog( CloudTrailClient cloudTrailClientClient, String trailName) {

        try {
            StartLoggingRequest loggingRequest = StartLoggingRequest.builder()
                    .name(trailName)
                    .build() ;

            cloudTrailClientClient.startLogging(loggingRequest);
            System.out.println(trailName +" has started logging");

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.logging.main]
 }
