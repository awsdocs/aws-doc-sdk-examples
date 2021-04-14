// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetTrailStatus.java demonstrates how to look up time information about a trail.]
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
                "Usage:\n" +
                "    GetTrailLoggingTime <trailName>  \n\n" +
                "Where:\n" +
                "    trailName - the name of the trail. \n" ;

         if (args.length != 1) {
             System.out.println(USAGE);
             System.exit(1);
         }

        String trailName = args[0] ;
        Region region = Region.US_EAST_1;
        CloudTrailClient cloudTrailClient = CloudTrailClient.builder()
                .region(region)
                .build();

        getLogTime(cloudTrailClient, trailName) ;
        cloudTrailClient.close();
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
