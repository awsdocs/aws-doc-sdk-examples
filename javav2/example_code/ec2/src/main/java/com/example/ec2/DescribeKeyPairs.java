//snippet-sourcedescription:[DescribeKeyPairs.java demonstrates how to get information about all instance key pairs.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.ec2;
// snippet-start:[ec2.java2.describe_key_pairs.complete]

// snippet-start:[ec2.java2.describe_key_pairs.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.KeyPairInfo;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.describe_key_pairs.import]

/**
 * Describes all instance key pairs
 */
public class DescribeKeyPairs {

    public static void main(String[] args) {
        // snippet-start:[ec2.java2.describe_key_pairs.main]
        Ec2Client ec2 = Ec2Client.create();
        try {
            DescribeKeyPairsResponse response = ec2.describeKeyPairs();

            for(KeyPairInfo keyPair : response.keyPairs()) {
                System.out.printf(
                    "Found key pair with name %s " +
                            "and fingerprint %s",
                    keyPair.keyName(),
                    keyPair.keyFingerprint());
             System.out.println("");
            }
        } catch (Ec2Exception e) {
            e.getStackTrace();
         }
        // snippet-end:[ec2.java2.describe_key_pairs.main]
    }
}
// snippet-end:[ec2.java2.describe_key_pairs.complete]
