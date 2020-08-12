//snippet-sourcedescription:[ListGrants.java demonstrates how to get information about the grants on an AWS KMS customer key.]
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

        final String USAGE =
                "To run this example, supply a key id value\n" +
                        "Usage: ListGrants <key-id>\n" +
                        "Example: ListGrants 1234abcd-12ab-34cd-56ef-1234567890ab \n";

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
