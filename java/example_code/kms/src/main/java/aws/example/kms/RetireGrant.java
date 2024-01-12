// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.RetireGrantRequest;
import com.amazonaws.services.kms.model.RetireGrantResult;

public class RetireGrant {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Retire a grant
        //
        String grantToken = "Place your grant token here";

        RetireGrantRequest request = new RetireGrantRequest().withGrantToken(grantToken);
        RetireGrantResult response = kmsClient.retireGrant(request);
    }
}