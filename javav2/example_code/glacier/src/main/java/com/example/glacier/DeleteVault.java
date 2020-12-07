//snippet-sourcedescription:[DeleteVault.java demonstrates how to delete an Amazon Simple Storage Service Glacier (Amazon S3 Glacier) vault.]
///snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3 Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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
                "Usage: " +
                "DeleteVault <vaultName>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault to delete. \n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteGlacierVault(glacier, vaultName);
        glacier.close();
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
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);

        }
        // snippet-end:[glacier.java2.delete.main]
    }
}
