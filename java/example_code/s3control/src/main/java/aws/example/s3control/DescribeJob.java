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

// snippet-sourcedescription:[DescribeJob.java demonstrates how to retrieve the ARN and status of a current job.]
// snippet-service:[s3]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-04-30]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
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
        String accountId = "Account Number";
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




