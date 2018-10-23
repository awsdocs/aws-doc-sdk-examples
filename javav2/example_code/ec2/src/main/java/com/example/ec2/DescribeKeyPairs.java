//snippet-sourcedescription:[DescribeKeyPairs.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
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
package com.example.ec2;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeKeyPairsResponse;
import software.amazon.awssdk.services.ec2.model.KeyPairInfo;

/**
 * Describes all instance key pairs
 */
public class DescribeKeyPairs
{
    public static void main(String[] args)
    {
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
    }
}

