// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetDetector.java demonstrates how to obtain a detector using its ID value.]
// snippet-service:[Amazon GuardDuty]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon GuardDuty]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-17]
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

package com.example.guardduty;

//snippet-start:[guard.java2.get_detector.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.GetDetectorRequest;
import software.amazon.awssdk.services.guardduty.model.GetDetectorResponse;
import software.amazon.awssdk.services.guardduty.model.GuardDutyException;
//snippet-end:[guard.java2.get_detector.import]

public class GetDetector {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the detector ID value. \n" +
                "\n" +
                "Example: GetDetector <detectorId>\n";

        if (args.length < 1) {
           System.out.println(USAGE);
           System.exit(1);
        }

        /* Read the name from command args */
        String detectorId = args[0];

        Region region = Region.US_EAST_1;
        GuardDutyClient guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .build();

        getSpecificDetector(guardDutyClient, detectorId);
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
