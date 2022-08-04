//snippet-sourcedescription:[DeleteAlias.java demonstrates how to delete an AWS Key Management Service (AWS KMS) alias.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kms;

// snippet-start:[kms.java2_delete_alias.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DeleteAliasRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_delete_alias.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteAlias {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <aliasName> \n\n" +
            "Where:\n" +
            "    aliasName - An alias name to delete (for example, alias/myAlias). \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String aliasName = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        deleteSpecificAlias(kmsClient, aliasName );
        kmsClient.close();
    }

    // snippet-start:[kms.java2_delete_alias.main]
    public static void deleteSpecificAlias(KmsClient kmsClient, String aliasName) {

        try {
            DeleteAliasRequest deleteAliasRequest = DeleteAliasRequest.builder()
                .aliasName(aliasName)
                .build();

            kmsClient.deleteAlias(deleteAliasRequest);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_delete_alias.main]
}
