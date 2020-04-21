//snippet-sourcedescription:[CreateKeyPair.java demonstrates how to create an Amazon EC2 key pair.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/11/2020]
//snippet-sourceauthor:[scmacdon]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-start:[ec2.java2.create_key_pair.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.CreateKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.create_key_pair.import]

/**
 * Creates an EC2 key pair
 */
public class CreateKeyPair {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a key pair name\n" +
                        "Ex: CreateKeyPair <key-pair-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyName = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        createEC2KeyPair(ec2, keyName) ;
    }

    // snippet-start:[ec2.java2.create_key_pair.main]
    public static void createEC2KeyPair(Ec2Client ec2,String keyName ) {

        try {
            CreateKeyPairRequest request = CreateKeyPairRequest.builder()
                .keyName(keyName).build();

            CreateKeyPairResponse response = ec2.createKeyPair(request);
            System.out.printf(
                "Successfully created key pair named %s",
                keyName);

        } catch (Ec2Exception e) {
        e.getStackTrace();
        }
        // snippet-end:[ec2.java2.create_key_pair.main]
      }
    }
