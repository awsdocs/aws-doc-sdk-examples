// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListDetectors.java demonstrates how to List detector id valuess of all the existing Amazon GuardDuty detector resources.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon GuardDuty]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2020]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
        guardDutyClient.close();
    }

    //snippet-start:[guard.java2.list_detectors.main]
    public static void listAllDetectors(GuardDutyClient guardDutyClient) {

        try {
            ListDetectorsResponse response = guardDutyClient.listDetectors();
            List<String> detectors = response.detectorIds();

            for (String detector: detectors) {
                System.out.println("The detector id is : "+detector);
            }

        } catch (GuardDutyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[guard.java2.list_detectors.main]
}
