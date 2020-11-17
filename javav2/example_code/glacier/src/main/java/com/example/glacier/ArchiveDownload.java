//snippet-sourcedescription:[ArchiveDownload.java demonstrates how to create a job start to retrieve inventory for an Amazon Simple Storage Service Glacier (Amazon S3 Glacier) vault.]
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

// snippet-start:[glacier.java2.download.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.JobParameters;
import software.amazon.awssdk.services.glacier.model.InitiateJobResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.download.import]

public class ArchiveDownload {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "ArchiveDownload - start a job to retrieve vault inventory\n\n" +
                "Usage: ArchiveDownload <vaultName> <accountId>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault.\n" +
                "  accountId - the account ID value.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String vaultName = args[0];
        String accountId = args[1];

         GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createJob(glacier, vaultName, accountId) ;
        glacier.close();
    }

    // snippet-start:[glacier.java2.download.main]
    public static void createJob(GlacierClient glacier, String vaultName, String accountId) {

        try {

            JobParameters job = JobParameters.builder()
                    .type("inventory-retrieval")
                    .build();

            InitiateJobRequest initJob = InitiateJobRequest.builder()
                    .jobParameters(job)
                    .accountId(accountId)
                    .vaultName(vaultName)
                    .build();

            InitiateJobResponse response = glacier.initiateJob(initJob);

            System.out.println("The job ID is: " +response.jobId()) ;
            System.out.println("The relative URI path of the job is: " +response.location()) ;

        } catch(GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);

        }
        System.out.println("Done");
    }
    // snippet-end:[glacier.java2.download.main]
}
