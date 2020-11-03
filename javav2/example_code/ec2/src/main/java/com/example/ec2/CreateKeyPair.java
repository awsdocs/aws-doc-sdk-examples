//snippet-sourcedescription:[CreateKeyPair.java demonstrates how to create an Amazon Elastic Compute Cloud (Amazon EC2) key pair.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/01/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
 * Creates an EC2 key pair.
 */
public class CreateKeyPair {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "CreateInstance <keyName> \n\n" +
                "Where:\n" +
                "    keyName - a key pair name (for example, TestKeyPair). \n\n"  ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line argument
        String keyName = args[0];

        // Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        createEC2KeyPair(ec2, keyName) ;
        ec2.close();
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
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[ec2.java2.create_key_pair.main]
      }
    }
