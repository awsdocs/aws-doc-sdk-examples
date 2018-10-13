//snippet-sourceauthor: [soo-aws]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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

        AmazonGuardDuty guardduty =
            AmazonGuardDutyClientBuilder.defaultClient();
        
        try {
            ListDetectorsRequest request = new ListDetectorsRequest();
            
            ListDetectorsResult response = guardduty.listDetectors(request);
            
            for (String detectorId : response.getDetectorIds())
            {
                System.out.println("DetectorId: " + detectorId );
            }
        } catch (AmazonServiceException ase) {
                System.out.println("Caught Exception: " + ase.getMessage());
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
        }

    }
}

