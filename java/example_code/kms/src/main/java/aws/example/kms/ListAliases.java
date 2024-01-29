// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;

public class ListAliases {
    public static void main(String[] args) {
        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // List the aliases in this AWS account
        //
        Integer limit = 10;

        String nextMarker = null;
        do {
            ListAliasesRequest req = new ListAliasesRequest()
                    .withMarker(nextMarker).withLimit(limit);
            ListAliasesResult result = kmsClient.listAliases(req);
            for (AliasListEntry alias : result.getAliases()) {
                System.out.printf("Found an alias named \"%s\".%n", alias.getAliasName());
            }
            nextMarker = result.getNextMarker();
        } while (nextMarker != null);

    }
}
