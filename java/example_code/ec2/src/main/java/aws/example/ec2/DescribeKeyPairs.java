/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package aws.example.ec2;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;

/**
 * Describes all instance key pairs
 */
public class DescribeKeyPairs
{
    public static void main(String[] args)
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeKeyPairsResult response = ec2.describeKeyPairs();

        for(KeyPairInfo key_pair : response.getKeyPairs()) {
            System.out.printf(
                "Found key pair with name %s " +
                "and fingerprint %s",
                key_pair.getKeyName(),
                key_pair.getKeyFingerprint());
        }
    }
}

