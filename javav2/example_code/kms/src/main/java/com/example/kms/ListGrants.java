//snippet-sourcedescription:[ListGrants.java demonstrates how to get information about AWS Key Management Service (AWS KMS) grants related to a key.]
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

// snippet-start:[kms.java2_list_grant.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.GrantListEntry;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListGrantsRequest;
import software.amazon.awssdk.services.kms.model.ListGrantsResponse;
import java.util.List;
// snippet-end:[kms.java2_list_grant.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListGrants {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <keyId> \n\n" +
                "Where:\n" +
                "    keyId - a key id value to use (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;

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

        displayGrantIds(kmsClient, keyId);
        kmsClient.close();
    }

    // snippet-start:[kms.java2_list_grant.main]
    public static void displayGrantIds(KmsClient kmsClient, String keyId) {

     try {
        ListGrantsRequest grantsRequest = ListGrantsRequest.builder()
                .keyId(keyId)
                .limit(15)
                .build();

        ListGrantsResponse response = kmsClient.listGrants(grantsRequest);
        List<GrantListEntry> grants = response.grants();
        for ( GrantListEntry grant: grants) {
            System.out.println("The grant Id is : "+grant.grantId());
        }

     } catch (KmsException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[kms.java2_list_grant.main]
}
