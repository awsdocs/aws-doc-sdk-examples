//snippet-sourcedescription:[ListVaults.java demonstrates how to list your Amazon Glacier vaults.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[glacier]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
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
public class ListVaults
{
    public static void main(String[] args)
    {
        final AmazonGlacier glacier = AmazonGlacierClientBuilder.defaultClient();
        ListVaultsRequest request = new ListVaultsRequest();

        boolean list_complete = false;
        int total_vaults = 0;
        System.out.println("Your Amazon Glacier vaults:");
        while (!list_complete) {
            ListVaultsResult result = glacier.listVaults(request);
            List<DescribeVaultOutput> vault_list = result.getVaultList();
            for (DescribeVaultOutput v: vault_list) {
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
