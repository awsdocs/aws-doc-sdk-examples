//snippet-sourcedescription:[CreateAlias.java demonstrates how to create an AWS Key Management Service (AWS KMS) alias.]
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

// snippet-start:[kms.java2_create_alias.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_create_alias.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateAlias {

    public static void main(String[] args) {

         final String usage = "\n" +
                "Usage:\n" +
                "    <targetKeyId> <aliasName> \n\n" +
                "Where:\n" +
                "    targetKeyId - The key ID or the Amazon Resource Name (ARN) of the customer master key (CMK). \n\n" +
                "    aliasName - An alias name (for example, alias/myAlias). \n\n" ;

         if (args.length != 2) {
              System.out.println(usage);
             System.exit(1);
         }

        String targetKeyId = args[0];
        String aliasName = args[1];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createCustomAlias(kmsClient, targetKeyId, aliasName);
        kmsClient.close();
    }

    // snippet-start:[kms.java2._create_alias.main]
    public static void createCustomAlias(KmsClient kmsClient, String targetKeyId, String aliasName) {

        try {
            CreateAliasRequest aliasRequest = CreateAliasRequest.builder()
                .aliasName(aliasName)
                .targetKeyId(targetKeyId)
                .build();

            kmsClient.createAlias(aliasRequest);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2._create_alias.main]
}
