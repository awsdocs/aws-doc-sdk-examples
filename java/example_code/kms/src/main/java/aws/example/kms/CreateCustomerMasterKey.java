// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;

public class CreateCustomerMasterKey {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create a CMK

        String desc = "Key for protecting critical data";

        CreateKeyRequest req = new CreateKeyRequest().withDescription(desc);
        CreateKeyResult result = kmsClient.createKey(req);

        System.out.printf(
                "Created a customer master key with id \"%s\"%n",
                result.getKeyMetadata().getArn());
    }

}
