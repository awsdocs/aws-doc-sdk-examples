// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.java.list_batch_jobs.complete]

package aws.example.s3control;

// snippet-start:[s3.java.list_batch_jobs.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3control.AWSS3Control;
import com.amazonaws.services.s3control.AWSS3ControlClient;
import com.amazonaws.services.s3control.model.JobListDescriptor;
import com.amazonaws.services.s3control.model.ListJobsRequest;
import com.amazonaws.services.s3control.model.ListJobsResult;

import static com.amazonaws.regions.Regions.US_WEST_2;
// snippet-end:[s3.java.list_batch_jobs.import]

public class ListJobs {
    public static void main(String[] args) {
        // snippet-start:[s3.java.list_batch_jobs.main]
        String accountId = "Account ID";

        try {
            AWSS3Control s3ControlClient = AWSS3ControlClient.builder()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(US_WEST_2)
                    .build();

            ListJobsResult result = s3ControlClient.listJobs(new ListJobsRequest()

                    .withAccountId(accountId)
                    .withMaxResults(20)
                    .withJobStatuses("Active", "Complete")).withJobs(new JobListDescriptor()
                            .withStatus("Active")
                            .withStatus("Complete"));

            for (JobListDescriptor jobSummary : result.getJobs()) {
                System.out.printf("%s - %s (status: %s)\n", jobSummary.getJobId(), jobSummary.getCreationTime(),
                        jobSummary.getStatus());
            }

            // snippet-end:[s3.java.list_batch_jobs.main]
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
// snippet-end:[s3.java.list_batch_jobs.complete]
