// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;

/**
 * Describes all instance key pairs
 */
public class DescribeKeyPairs {
    public static void main(String[] args) {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeKeyPairsResult response = ec2.describeKeyPairs();

        for (KeyPairInfo key_pair : response.getKeyPairs()) {
            System.out.printf(
                    "Found key pair with name %s " +
                            "and fingerprint %s",
                    key_pair.getKeyName(),
                    key_pair.getKeyFingerprint());
        }
    }
}
