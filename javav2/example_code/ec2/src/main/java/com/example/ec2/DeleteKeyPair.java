//snippet-sourcedescription:[DeleteKeyPair.java demonstrates how to delete an Amazon Elastic Compute Cloud (Amazon EC2) key pair.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ec2;

// snippet-start:[ec2.java2.delete_key_pair.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteKeyPairRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
// snippet-end:[ec2.java2.delete_key_pair.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteKeyPair {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "   <keyPair> \n\n" +
                "Where:\n" +
                "   keyPair - A key pair name (for example, TestKeyPair).";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String keyPair = args[0];
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
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

           ec2.deleteKeyPair(request);
           System.out.printf(
                "Successfully deleted key pair named %s", keyPair);

        } catch (Ec2Exception e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
    }
    // snippet-end:[ec2.java2.delete_key_pair.main]
}
