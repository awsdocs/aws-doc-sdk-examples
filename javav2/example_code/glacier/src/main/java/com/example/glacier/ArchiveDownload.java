//snippet-sourcedescription:[ArchiveDownload.java demonstrates how to create a job start to retrieve inventory for an Amazon Glacier vault.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glacier;

// snippet-start:[glacier.java2.download.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.JobParameters;
import software.amazon.awssdk.services.glacier.model.InitiateJobResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.DescribeJobRequest;
import software.amazon.awssdk.services.glacier.model.DescribeJobResponse;
import software.amazon.awssdk.services.glacier.model.GetJobOutputRequest;
import software.amazon.awssdk.services.glacier.model.GetJobOutputResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
// snippet-end:[glacier.java2.download.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ArchiveDownload {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <vaultName> <accountId> <path>\n\n" +
                "Where:\n" +
                "   vaultName - The name of the vault.\n" +
                "   accountId - The account ID value.\n\n"+
                "   path - The path where the file is written to.\n\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String vaultName = args[0];
        String accountId = args[1];
        String path = args[2];
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String jobNum = createJob(glacier, vaultName, accountId) ;
        checkJob(glacier, jobNum,vaultName,accountId, path);
        glacier.close();
    }

    // snippet-start:[glacier.java2.download.main]
    public static String createJob(GlacierClient glacier, String vaultName, String accountId) {

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
            return response.jobId();

        } catch(GlacierException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);

        }
        return "";
    }

    //  Poll S3 Glacier = Polling a Job may take 4-6 hours according to the Documentation.
    public static void checkJob(GlacierClient glacier, String jobId, String name, String account, String path) {

       try{
            Boolean finished = false;
            String jobStatus;
            int yy=0;

            while (!finished) {
                DescribeJobRequest jobRequest = DescribeJobRequest.builder()
                        .jobId(jobId)
                        .accountId(account)
                        .vaultName(name)
                        .build();

                DescribeJobResponse response = glacier.describeJob(jobRequest);
                jobStatus = response.statusCodeAsString();

               if (jobStatus.compareTo("Succeeded") == 0)
                   finished = true;
               else {
                   System.out.println(yy + " status is: " + jobStatus);
                   Thread.sleep(1000);
               }
               yy++;
           }

           System.out.println("Job has Succeeded");
           GetJobOutputRequest jobOutputRequest = GetJobOutputRequest.builder()
                   .jobId(jobId)
                   .vaultName(name)
                   .accountId(account)
                   .build();

           ResponseBytes<GetJobOutputResponse> objectBytes = glacier.getJobOutputAsBytes(jobOutputRequest);

           // Write the data to a local file.
           byte[] data = objectBytes.asByteArray();
           File myFile = new File(path);
           OutputStream os = new FileOutputStream(myFile);
           os.write(data);
           System.out.println("Successfully obtained bytes from a Glacier vault");
           os.close();

       } catch(GlacierException | InterruptedException | IOException e) {
           System.out.println(e.getMessage());
           System.exit(1);

       }
    }
    // snippet-end:[glacier.java2.download.main]
}