// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListDetectors.java demonstrates how to list detector ID valuess of all the existing Amazon GuardDuty detector resources.]
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

//snippet-start:[guard.java2.list_detectors.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.GuardDutyException;
import software.amazon.awssdk.services.guardduty.model.ListDetectorsResponse;
import java.util.List;
//snippet-end:[guard.java2.list_detectors.import]

public class ListDetectors {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GuardDutyClient guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .build();

        listAllDetectors(guardDutyClient);
    }

    //snippet-start:[guard.java2.list_detectors.main]
    public static void listAllDetectors(GuardDutyClient guardDutyClient) {

        try {
            ListDetectorsResponse response = guardDutyClient.listDetectors();
            List<String> detectors = response.detectorIds();

            for (String detector: detectors) {
                System.out.println("The detector ID is : "+detector);
            }

        } catch (GuardDutyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[guard.java2.list_detectors.main]
}
