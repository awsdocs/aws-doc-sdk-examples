//snippet-sourcedescription:[ArchiveDownload.java demonstrates how to create a job start to retrieve vault inventory.]
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
                "  accountId - the account id.\n\n";

           if (args.length < 2) {
               System.out.println(USAGE);
              System.exit(1);
          }

        String vaultName = args[0];
        String accountId = args[1];

        // Create a GlacierClient object
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createJob(glacier, vaultName, accountId) ;
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

            InitiateJobResponse reponse = glacier.initiateJob(initJob);

            System.out.println("The Job ID is: " +reponse.jobId()) ;
            System.out.println("The relative URI path of the job is: "+reponse.location()) ;

    } catch(GlacierException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);

    }
        System.out.println("Done");
    }
    // snippet-end:[glacier.java2.download.main]
}
