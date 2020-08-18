//snippet-sourcedescription:[CreateGrant.java demonstrates how to add a grant to a CMK that specifies the CMK's use.]
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

// snippet-start:[kms.java2_create_grant.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateGrantRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_create_grant.import]

public class CreateGrant {

    public static void main(String[] args) {
        final String USAGE =
                "To run this example, supply a key ID or ARN, a grantee principal" +
                        ", and an operation\n" +
                        "Usage: CreateGrant <key-id> <grantee-principal> <operation>\n" +
                        "Example: CreateGrant 1234abcd-12ab-34cd-56ef-1234567890ab " +
                        "arn:aws:iam::111122223333:user/Alice Encrypt\n";

           if (args.length != 3) {
               System.out.println(USAGE);
               System.exit(1);
           }

        String keyId = args[0];
        String granteePrincipal = args[1];
        String operation = args[2];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        String grantId = createGrant(kmsClient, keyId, granteePrincipal, operation);
        System.out.printf("Successfully created a grant with ID %s%n", grantId);
    }

    // snippet-start:[kms.java2_create_grant.main]
    public static String createGrant(KmsClient kmsClient, String keyId, String granteePrincipal, String operation) {

        try {
        CreateGrantRequest grantRequest = CreateGrantRequest.builder()
                .keyId(keyId)
                .granteePrincipal(granteePrincipal)
                .operationsWithStrings(operation)
                .build();

        CreateGrantResponse response = kmsClient.createGrant(grantRequest);
        return response.grantId();

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[kms.java2_create_grant.main]
}
