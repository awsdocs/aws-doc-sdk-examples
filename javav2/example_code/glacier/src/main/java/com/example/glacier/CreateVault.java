//snippet-sourcedescription:[CreateVault.java demonstrates how to create an Amazon Simple Storage Service Glacier (Amazon S3 Glacier) vault.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glacier;

// snippet-start:[glacier.java2.create.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.CreateVaultRequest;
import software.amazon.awssdk.services.glacier.model.CreateVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.create.import]

public class CreateVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateVault <vaultName>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault to create.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createGlacierVault(glacier, vaultName);
        glacier.close();
    }

    // snippet-start:[glacier.java2.create.main]
    public static void createGlacierVault(GlacierClient glacier, String vaultName ) {

        try {
            CreateVaultRequest vaultRequest = CreateVaultRequest.builder()
                    .vaultName(vaultName)
                    .build();

            CreateVaultResponse createVaultResult = glacier.createVault(vaultRequest);
            System.out.println("The URI of the new vault is " + createVaultResult.location());
        } catch(GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);

        }
        // snippet-end:[glacier.java2.create.main]
    }
}
