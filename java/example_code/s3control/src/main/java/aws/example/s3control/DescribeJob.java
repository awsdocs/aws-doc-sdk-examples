// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.java.describe_job.complete]

package aws.example.s3control;

// snippet-start:[s3.java.describe_job.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3control.AWSS3Control;
import com.amazonaws.services.s3control.AWSS3ControlClient;
import com.amazonaws.services.s3control.model.DescribeJobRequest;
import com.amazonaws.services.s3control.model.DescribeJobResult;
import com.amazonaws.services.s3control.model.JobDescriptor;

import static com.amazonaws.regions.Regions.US_WEST_2;
// snippet-end:[s3.java.describe_job.import]

public class DescribeJob {
    public static void main(String[] args) {
        // snippet-start:[s3.java.describe_job.main]
        String accountId = "Account ID";
        String jobId = "00e123a4-c0d8-41f4-a0eb-b46f9ba5b07c";

        try {
            AWSS3Control s3ControlClient = AWSS3ControlClient.builder()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(US_WEST_2)
                    .build();

            DescribeJobResult result = s3ControlClient.describeJob(new DescribeJobRequest()
                    .withAccountId(accountId)
                    .withJobId(jobId));

            JobDescriptor job = result.getJob();
            System.out.printf("%s - %s (status: %s)\n", job.getJobId(), job.getJobArn(), job.getStatus());

            // snippet-end:[s3.java.describe_job.main]
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
// snippet-end:[s3.java.describe_job.complete]
