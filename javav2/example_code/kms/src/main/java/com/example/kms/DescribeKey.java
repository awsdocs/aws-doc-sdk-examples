//snippet-sourcedescription:[DescribeKey.java demonstrates how to obtain information about an AWS Key Management Service (AWS KMS) key.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kms;

// snippet-start:[kms.java2_describe_key.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_describe_key.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeKey {

    public static void main(String[] args) {

       final String usage = "\n" +
                "Usage:\n" +
                "    <keyId> \n\n" +
                "Where:\n" +
                "    keyId -  A key id value to describe (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;


        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String keyId = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        describeSpecifcKey(kmsClient, keyId );
        kmsClient.close();
    }

    // snippet-start:[kms.java2_describe_key.main]
    public static void describeSpecifcKey(KmsClient kmsClient,String keyId ){

       try {
            DescribeKeyRequest keyRequest = DescribeKeyRequest.builder()
                .keyId(keyId)
                .build();

            DescribeKeyResponse response = kmsClient.describeKey(keyRequest);
            System.out.println("The key description is "+response.keyMetadata().description());
            System.out.println("The key ARN is "+response.keyMetadata().arn());

       } catch (KmsException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
    }
    // snippet-end:[kms.java2_describe_key.main]
}