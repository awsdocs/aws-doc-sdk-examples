// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetFindings.java demonstrates how to List detector id values for existing Amazon GuardDuty detector resources.]
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

//snippet-start:[guard.java2.get_findings.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.Finding;
import software.amazon.awssdk.services.guardduty.model.GetFindingsRequest;
import software.amazon.awssdk.services.guardduty.model.GetFindingsResponse;
import software.amazon.awssdk.services.guardduty.model.GuardDutyException;
import java.util.ArrayList;
import java.util.List;
//snippet-end:[guard.java2.get_findings.import]

public class GetFindings {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the findingId value and the detectorId value.  \n" +
                "\n" +
                "Ex: GetFindings <findingId><detectorId>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String findingId = args[0];
        String detectorId = args[1];

        Region region = Region.US_EAST_1;
        GuardDutyClient guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .build();

        getSpecificFinding(guardDutyClient, findingId, detectorId);
        guardDutyClient.close();
    }

    //snippet-start:[guard.java2.get_findings.main]
    public static void getSpecificFinding(GuardDutyClient guardDutyClient, String findingId, String detectorId) {

        try {
            List<String> myIds = new ArrayList<>();
            myIds.add(findingId);

            GetFindingsRequest findingsRequest = GetFindingsRequest.builder()
                    .findingIds(myIds)
                    .detectorId(detectorId)
                    .build();

            GetFindingsResponse findingsResponse = guardDutyClient.getFindings(findingsRequest);
            List<Finding> findings = findingsResponse.findings();

            for (Finding finding : findings) {
                System.out.println("The finding ARN is " + finding.arn());
                System.out.println("The resource type is " + finding.resource().resourceType());
            }

        } catch (GuardDutyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[guard.java2.get_findings.main]
}
