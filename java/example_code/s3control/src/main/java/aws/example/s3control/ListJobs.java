/**
 * Copyright 2018-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

// snippet-sourcedescription:[ListJobs.java demonstrates how to list active and complete Amazon S3 Batch Operations.]
// snippet-service:[s3]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-04-30]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
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

import static com.amazonaws.regions.Regions.US_WEST_2;
// snippet-end:[s3.java.list_batch_jobs.import]

public class ListJobs {
    public static void main(String[] args) {
        // snippet-start:[s3.java.list_batch_jobs.main]
        String accountId = "Account Number";

        try {
            AWSS3Control s3ControlClient = AWSS3ControlClient.builder()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(US_WEST_2)
                    .build();


            ListJobsResult result = s3ControlClient.listJobs(new ListJobsRequest()

                    .withAccountId(accountId)
                    .withMaxResults(20)
                    .withJobStatuses("Active", "Complete")
            ).withJobs(new JobListDescriptor()
                    .withStatus("Active")
                    .withStatus("Complete"));
            
            for (JobListDescriptor jobSummary : result.getJobs()) {
                    System.out.printf("%s - %s (status: %s)\n", jobSummary.getJobId(), jobSummary.getJobArn(), jobSummary.getStatus());
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
