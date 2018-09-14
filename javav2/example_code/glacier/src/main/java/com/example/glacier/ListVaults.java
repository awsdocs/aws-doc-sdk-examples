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
package com.example.glacier;
import software.amazon.awssdk.services.glacier.model.ListVaultsRequest;
import software.amazon.awssdk.services.glacier.model.ListVaultsResponse;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultOutput;
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
        GlacierClient glacier = GlacierClient.builder().build();

        boolean list_complete = false;
        String new_marker = null;
        int total_vaults = 0;
        System.out.println("Your Amazon Glacier vaults:");
        while (!list_complete) {
            ListVaultsResponse response = null;

        	if (new_marker != null) {
        		ListVaultsRequest request = ListVaultsRequest.builder()
        				.marker(new_marker)
        				.build();
                response = glacier.listVaults(request);
        	}
        	else {
        		ListVaultsRequest request = ListVaultsRequest.builder()
        				.build();
                response = glacier.listVaults(request);
        	}
        	
            List<DescribeVaultOutput> vault_list = response.vaultList();
            for (DescribeVaultOutput v: vault_list) {
                total_vaults += 1;
                System.out.println("* " + v.vaultName());
            }
            // check for further results.
            new_marker = response.marker();
            if (new_marker == null) {
            	list_complete = true;
            }
        }

        if (total_vaults == 0) {
            System.out.println("  no vaults found.");
        }
    }
}

