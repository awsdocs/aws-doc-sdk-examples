// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTrailStatus.java demonstrates how to look up time information about a trail.]
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

//snippet-start:[cloudtrail.java2.getLogTime.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.model.CloudTrailException;
import software.amazon.awssdk.services.cloudtrail.model.GetTrailStatusRequest;
import software.amazon.awssdk.services.cloudtrail.model.GetTrailStatusResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
//snippet-end:[cloudtrail.java2.getLogTime.import]

public class GetTrailLoggingTime {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the trail.  \n" +
                "\n" +
                "Example: GetTrailStatus <trailName>\n";

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

        getLogTime(cloudTrailClientClient, trailName) ;
    }

    //snippet-start:[cloudtrail.java2.getLogTime.main]
    public static void getLogTime(CloudTrailClient cloudTrailClientClient, String trailName) {

        try {
            GetTrailStatusRequest trailStatusRequest = GetTrailStatusRequest.builder()
                    .name(trailName)
                    .build();

            GetTrailStatusResponse trailStatusResponse = cloudTrailClientClient.getTrailStatus(trailStatusRequest);
            Instant lastestNotication = trailStatusResponse.startLoggingTime();

            if (lastestNotication != null) {
                // Convert the Instant to readable date
                DateTimeFormatter formatter =
                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                                .withLocale(Locale.US)
                                .withZone(ZoneId.systemDefault());

                formatter.format(lastestNotication);
                System.out.println("The date of the logging time is " + lastestNotication);
            } else {
                System.out.println("Logging time is not defined");
            }

        } catch (CloudTrailException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cloudtrail.java2.getLogTime.main]
}
