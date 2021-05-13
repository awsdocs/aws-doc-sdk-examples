//snippet-sourcedescription:[CreateGrant.java demonstrates how to add a grant to a customer master key (CMK).]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.kms;

// snippet-start:[kms.java2_create_grant.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateGrantRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_create_grant.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateGrant {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateGrant <keyId> <granteePrincipal> <operation> \n\n" +
                "Where:\n" +
                "    keyId - the unique identifier for the customer master key (CMK) that the grant applies to. \n\n" +
                "    granteePrincipal - the principal that is given permission to perform the operations that the grant permits. \n\n" +
                "    operation - an operation (for example, Encrypt). \n\n" ;

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
        kmsClient.close();
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
