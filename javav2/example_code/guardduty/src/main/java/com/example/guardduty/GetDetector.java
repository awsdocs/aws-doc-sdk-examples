// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetDetector.java demonstrates how to obtain a detector using its id value.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon GuardDuty]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.guardduty;

//snippet-start:[guard.java2.get_detector.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.GetDetectorRequest;
import software.amazon.awssdk.services.guardduty.model.GetDetectorResponse;
import software.amazon.awssdk.services.guardduty.model.GuardDutyException;
//snippet-end:[guard.java2.get_detector.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDetector {

    public static void main(String[] args) {

        final String usage = "\n" +
                "To run this example, supply the detector Id value. \n" +
                "\n" +
                "Ex: GetDetector <detectorId>\n";

        if (args.length != 1) {
           System.out.println(usage);
           System.exit(1);
        }

        String detectorId = args[0];
        Region region = Region.US_EAST_1;
        GuardDutyClient guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getSpecificDetector(guardDutyClient, detectorId);
        guardDutyClient.close();
    }

    //snippet-start:[guard.java2.get_detector.main]
    public static void getSpecificDetector(GuardDutyClient guardDutyClient, String detectorId) {

        try {
            GetDetectorRequest detectorRequest = GetDetectorRequest.builder()
                .detectorId(detectorId)
                .build();

            GetDetectorResponse detectorResponse = guardDutyClient.getDetector(detectorRequest);
            System.out.println("The detector status is "+detectorResponse.status().toString());

    } catch (GuardDutyException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
 }
    //snippet-end:[guard.java2.get_detector.main]
}
