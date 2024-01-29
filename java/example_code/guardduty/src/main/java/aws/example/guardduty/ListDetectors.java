// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.guardduty;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.guardduty.AmazonGuardDuty;
import com.amazonaws.services.guardduty.AmazonGuardDutyClientBuilder;
import com.amazonaws.services.guardduty.model.*;

/**
 * List GuardDuty Detectors in the current AWS Region
 */
public class ListDetectors {
    public static void main(String[] args) {

        AmazonGuardDuty guardduty = AmazonGuardDutyClientBuilder.defaultClient();

        try {
            ListDetectorsRequest request = new ListDetectorsRequest();

            ListDetectorsResult response = guardduty.listDetectors(request);

            for (String detectorId : response.getDetectorIds()) {
                System.out.println("DetectorId: " + detectorId);
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught Exception: " + ase.getMessage());
            System.out.println("Response Status Code: " + ase.getStatusCode());
            System.out.println("Error Code: " + ase.getErrorCode());
            System.out.println("Request ID: " + ase.getRequestId());
        }

    }
}
