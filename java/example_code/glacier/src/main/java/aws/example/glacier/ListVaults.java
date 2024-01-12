// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.glacier;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import java.util.List;

/**
 * List your Amazon Glacier vaults.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListVaults {
    public static void main(String[] args) {
        final AmazonGlacier glacier = AmazonGlacierClientBuilder.defaultClient();
        ListVaultsRequest request = new ListVaultsRequest();

        boolean list_complete = false;
        int total_vaults = 0;
        System.out.println("Your Amazon Glacier vaults:");
        while (!list_complete) {
            ListVaultsResult result = glacier.listVaults(request);
            List<DescribeVaultOutput> vault_list = result.getVaultList();
            for (DescribeVaultOutput v : vault_list) {
                total_vaults += 1;
                System.out.println("* " + v.getVaultName());
            }
            // check for further results.
            String marker = result.getMarker();
            if (marker != null) {
                request.setMarker(marker);
            } else {
                list_complete = true;
            }
        }

        if (total_vaults == 0) {
            System.out.println("  no vaults found.");
        }
    }
}
