//snippet-sourcedescription:[CreateAlias.java demonstrates how to create an AWS KMS alias.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/10/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.*
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

package com.example.kms;

// snippet-start:[kms.java2_create_alias.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_create_alias.import]

public class CreateAlias {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a key ID or ARN and an alias name\n" +
                        "Usage: CreateAlias <key-id> <alias-name>\n" +
                        "Example: CreateAlias 1234abcd-12ab-34cd-56ef-1234567890ab " +
                        "alias/myAlias\n";

         if (args.length != 2) {
              System.out.println(USAGE);
             System.exit(1);
        }

        String targetKeyId = args[0];
        String aliasName = args[1];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        createCustomAlias(kmsClient, targetKeyId, aliasName);
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
        // snippet-end:[kms.java2._create_alias.main]
    }
}
