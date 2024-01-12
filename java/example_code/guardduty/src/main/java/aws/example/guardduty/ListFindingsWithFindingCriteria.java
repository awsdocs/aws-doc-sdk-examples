// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.guardduty;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.guardduty.AmazonGuardDuty;
import com.amazonaws.services.guardduty.AmazonGuardDutyClientBuilder;
import com.amazonaws.services.guardduty.model.*;

/**
 * List all GuardDuty Findings that match the specified FindingCriteria
 */
public class ListFindingsWithFindingCriteria {
    public static void main(String[] args) {

        AmazonGuardDuty guardduty = AmazonGuardDutyClientBuilder.defaultClient();

        // Set detectorId to the detectorId returned by GuardDuty's
        // ListDetectors() for your current AWS Account/Region
        final String detectorId = "ceb03b04f96520a8884c959f3e95c25a";

        FindingCriteria criteria = new FindingCriteria();
        Condition condition = new Condition();

        List<String> condValues = new ArrayList<String>();
        condValues.add("Recon:EC2/PortProbeUnprotectedPort");
        condValues.add("Recon:EC2/PortScan");
        condition.withEq(condValues);
        criteria.addCriterionEntry("type", condition);

        try {
            ListFindingsRequest request = new ListFindingsRequest()
                    .withDetectorId(detectorId)
                    .withFindingCriteria(criteria);

            ListFindingsResult response = guardduty.listFindings(request);

            for (String finding : response.getFindingIds()) {
                System.out.println("FindingId: " + finding);
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Response Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }

    }
}
