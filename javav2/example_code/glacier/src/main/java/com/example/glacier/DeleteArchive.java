//snippet-sourcedescription:[DeleteVault.java demonstrates how to delete an Amazon Simple Storage Service Glacier (Amazon S3 Glacier) archive.]
//snippet-keyword:[AWS SDK for Java v2]
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
import software.amazon.awssdk.services.glacier.model.DeleteArchiveRequest;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.delete.import]


public class DeleteArchive {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "DeleteArchive <vaultName> <accountId> <archiveId>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault that contains the archive to delete.\n\n" +
                "  accountId - the account ID value.\n\n"+
                "  archiveId - the archive ID value.\n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        String accountId = args[1];
        String archiveId = args[2];

        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteGlacierArchive(glacier, vaultName, accountId, archiveId);
        glacier.close();
    }

    // snippet-start:[glacier.java2.delete.main]
    public static void deleteGlacierArchive(GlacierClient glacier, String vaultName, String accountId, String archiveId) {

        try {
            DeleteArchiveRequest delArcRequest = DeleteArchiveRequest.builder()
                    .vaultName(vaultName)
                    .accountId(accountId)
                    .archiveId(archiveId)
                    .build();

            glacier.deleteArchive(delArcRequest);
            System.out.println("The vault was deleted!");
        } catch(GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);

        }
        // snippet-end:[glacier.java2.delete.main]
    }
}
