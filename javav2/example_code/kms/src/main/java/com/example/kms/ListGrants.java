//snippet-sourcedescription:[ListGrants.java demonstrates how to get information about AWS Key Management Service (AWS KMS) grants related to a key.]
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

// snippet-start:[kms.java2_list_grant.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.GrantListEntry;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListGrantsRequest;
import software.amazon.awssdk.services.kms.model.ListGrantsResponse;
import java.util.List;
// snippet-end:[kms.java2_list_grant.import]

public class ListGrants {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListGrants <keyId> \n\n" +
                "Where:\n" +
                "    keyId - a key id value to use (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
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
