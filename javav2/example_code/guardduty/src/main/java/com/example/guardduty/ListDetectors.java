// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListDetectors.java demonstrates how to List detector id valuess of all the existing Amazon GuardDuty detector resources.]
//snippet-keyword:[AWS SDK for Java v2]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.guardduty;

//snippet-start:[guard.java2.list_detectors.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.GuardDutyException;
import software.amazon.awssdk.services.guardduty.model.ListDetectorsResponse;
import java.util.List;
//snippet-end:[guard.java2.list_detectors.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDetectors {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        GuardDutyClient guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
