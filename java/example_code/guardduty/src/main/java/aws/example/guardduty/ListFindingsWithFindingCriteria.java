//snippet-sourcedescription:[ListFindingsWithFindingCriteria.java demonstrates how to list all GuardDuty findings that match the specified finding criteria.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon GuardDuty]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[Keith Walker]
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

        AmazonGuardDuty guardduty =
            AmazonGuardDutyClientBuilder.defaultClient();

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
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
        }

    }
}
