//snippet-sourcedescription:[GetJobRun.java demonstrates how to get a job run request.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.glue;

//snippet-start:[glue.java2.get_job.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GetJobRunRequest;
import software.amazon.awssdk.services.glue.model.GetJobRunResponse;
import software.amazon.awssdk.services.glue.model.GlueException;
//snippet-end:[glue.java2.get_job.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetJobRun {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <jobName> <runId>\n\n" +
                "Where:\n" +
                "    jobName - The name of the job. \n" +
                "    runId - The run id value. \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String jobName = args[0];
        String runId = args[1];

        Region region = Region.US_EAST_1;
        GlueClient glueClient = GlueClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getGlueJobRun(glueClient, jobName, runId);
        glueClient.close();
    }

    //snippet-start:[glue.java2.get_job.main]
    public static void getGlueJobRun(GlueClient glueClient, String jobName, String runId) {

        try {
              GetJobRunRequest jobRunRequest = GetJobRunRequest.builder()
                .jobName(jobName)
                .runId(runId)
                .build();

              GetJobRunResponse runResponse = glueClient.getJobRun(jobRunRequest);
              System.out.println("Job status is : "+runResponse.jobRun().jobRunStateAsString());

        } catch (GlueException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[glue.java2.get_job.main]
}
