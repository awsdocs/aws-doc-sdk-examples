//snippet-sourcedescription:[DescribeKeyPairs.java demonstrates how to get information about all instance key pairs.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[ec2.java.describe_key_pairs.complete]
// snippet-start:[ec2.java.describe_key_pairs.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.KeyPairInfo;
 
// snippet-end:[ec2.java.describe_key_pairs.import]
/**
 * Describes all instance key pairs
 */
public class DescribeKeyPairs
{
    public static void main(String[] args)
    {
    	// snippet-start:[ec2.java.describe_key_pairs.main]
        Ec2Client ec2 = Ec2Client.create();

        DescribeKeyPairsResponse response = ec2.describeKeyPairs();

        for(KeyPairInfo key_pair : response.keyPairs()) {
            System.out.printf(
                "Found key pair with name %s " +
                "and fingerprint %s",
                key_pair.keyName(),
                key_pair.keyFingerprint());
            System.out.println("");
        }
        // snippet-end:[ec2.java.describe_key_pairs.main]
        
    }
}
 
// snippet-end:[ec2.java.describe_key_pairs.complete]
