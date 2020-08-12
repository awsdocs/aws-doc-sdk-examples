//snippet-sourcedescription:[DeleteAlias.java demonstrates how to delete a kms alias..]
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

// snippet-start:[kms.java2_delete_alias.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DeleteAliasRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-delete:[kms.java2_delete_alias.import]

public class DeleteAlias {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply an alias name\n" +
                        "Usage: DeleteAlias <alias-name>\n" +
                        "Example: DeleteAlias alias/myAlias\n";

         if (args.length != 1) {
              System.out.println(USAGE);
             System.exit(1);
         }

        String aliasName = args[0];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        deleteSpecificAlias(kmsClient, aliasName );
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
        // snippet-end:[kms.java2_delete_alias.main]
    }
}
