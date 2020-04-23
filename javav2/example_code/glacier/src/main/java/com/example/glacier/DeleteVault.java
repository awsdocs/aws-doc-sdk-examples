//snippet-sourcedescription:[DeleteVault.java demonstrates how to delete an Amazon Glacier vault.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/17/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.glacier;

// snippet-start:[glacier.java2.delete.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DeleteVaultRequest;
import software.amazon.awssdk.services.glacier.model.DeleteVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.delete.import]


public class DeleteVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DeleteVault - deletes an Amazon Glacier vault\n\n" +
                "Usage: DeleteVault <vaultName>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault to delete.\n\n";

       if (args.length < 1) {
           System.out.println(USAGE);
           System.exit(1);
       }

        String vaultName = args[0];

        // Create a GlacierClient object
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteGlacierVault(glacier, vaultName );
    }

    // snippet-start:[glacier.java2.delete.main]
    public static void deleteGlacierVault(GlacierClient glacier, String vaultName) {

        try {
            DeleteVaultRequest delVaultRequest = DeleteVaultRequest.builder()
                    .vaultName(vaultName)
                    .build();

            DeleteVaultResponse delVaultResult = glacier.deleteVault(delVaultRequest);
            System.out.println("The vault was deleted!");
        } catch(GlacierException e) {
            e.getStackTrace();
            System.exit(1);

        }
        // snippet-end:[glacier.java2.delete.main]
    }
}
