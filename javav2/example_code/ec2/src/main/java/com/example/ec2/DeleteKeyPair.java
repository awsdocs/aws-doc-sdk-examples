//snippet-sourcedescription:[DeleteKeyPair.java demonstrates how to delete an Amazon Elastic Compute Cloud (Amazon EC2) key pair.]
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

// snippet-start:[ec2.java2.delete_key_pair.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.delete_key_pair.import]

/**
 * Deletes a key pair.
 */
public class DeleteKeyPair {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "DeleteKeyPair <keyPair> \n\n" +
                "Where:\n" +
                "    keyPair - a key pair name (for example, TestKeyPair)."  ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        // Read the command line argument
        String keyPair = args[0];

        //Create an Ec2Client object
        Region region = Region.US_WEST_2;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        deleteKeys(ec2,keyPair);
        ec2.close();
    }

    // snippet-start:[ec2.java2.delete_key_pair.main]
    public static void deleteKeys(Ec2Client ec2, String keyPair) {

       try {

           DeleteKeyPairRequest request = DeleteKeyPairRequest.builder()
                .keyName(keyPair)
                .build();

           DeleteKeyPairResponse response = ec2.deleteKeyPair(request);

            // snippet-end:[ec2.java2.delete_key_pair.main]
            System.out.printf(
                "Successfully deleted key pair named %s", keyPair);

        } catch (Ec2Exception e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
    }
}
