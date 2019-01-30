//snippet-sourcedescription:[JobStatusNotificationsSample.java demonstrates how to use notifications to track the status of a job.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Transcoder]
//snippet-service:[elastictranscoder]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[elastictranscoder.java.job_status_notification_sample.import]
package com.amazonaws.services.elastictranscoder.samples;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.elastictranscoder.samples.model.JobStatusNotification;
import com.amazonaws.services.elastictranscoder.samples.model.JobStatusNotificationHandler;
import com.amazonaws.services.elastictranscoder.samples.utils.SqsQueueNotificationWorker;
import com.amazonaws.services.elastictranscoder.samples.utils.TranscoderSampleUtilities;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;


/**
 * The purpose of this sample is to show how job status notifications can be
 * used to receive job status updates using an event-driven model.  Using
 * notifications allows you to track job status of transcoding jobs in a
 * scalable fashion.
 * 
 * Note that this implementation will not scale to multiple machines because
 * the provided JobStatusNotificationHandler is looking for a specific job ID.
 * If there are multiple machines polling SQS for notifications, there is no
 * guarantee that a particular machine will receive a particular notification.
 * 
 * More information about notifications can be found in the Elastic Transcoder
 * documentation:
 * http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/notifications.html
 */
public class JobStatusNotificationsSample {

    // This is the ID of the Elastic Transcoder pipeline that was created when
    // setting up your AWS environment:
    // http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#java-pipeline
    private static final String PIPELINE_ID = "Enter your pipeline id here.";

    // This is the URL of the SQS queue that was created when setting up your
    // AWS environment.
    // http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#java-sqs
    private static final String SQS_QUEUE_URL = "Enter your queue url here.";
    
    // This is the name of the input key that you would like to transcode.
    private static final String INPUT_KEY = "Enter your input key here.";
    
    // This will generate a 480p 16:9 mp4 output.
    private static final String PRESET_ID = "1351620000001-000020";
    
    // All outputs will have this prefix prepended to their output key.
    private static final String OUTPUT_KEY_PREFIX = "elastic-transcoder-samples/output/";
    
    // Clients are built using the default credentials provider chain.  This
    // will attempt to get your credentials in the following order:
    //      1. Environment variables (AWS_ACCESS_KEY and AWS_SECRET_KEY).
    //      2. Java system properties (AwsCredentials.properties).
    //      3. Instance profile credentials on EC2 instances.
    private static final AmazonSQS amazonSqs = new AmazonSQSClient();
    private static final AmazonElasticTranscoder amazonElasticTranscoder = new AmazonElasticTranscoderClient();
    
    public static void main(String[] args) throws Exception {
        
        // Setup our notification worker.
        SqsQueueNotificationWorker sqsQueueNotificationWorker = new SqsQueueNotificationWorker(amazonSqs, SQS_QUEUE_URL);
        Thread notificationThread = new Thread(sqsQueueNotificationWorker);
        notificationThread.start();
        
        // Create a job in Elastic Transcoder.
        String jobId = createElasticTranscoderJob();
        
        // Wait for the job we created to complete.
        System.out.println("Waiting for job to complete: " + jobId);
        waitForJobToComplete(jobId, sqsQueueNotificationWorker);
    }
    
    /**
     * Creates a job in Elastic Transcoder using the configured pipeline, input
     * key, preset, and output key prefix.
     * @return Job ID of the job that was created in Elastic Transcoder.
     * @throws Exception
     */
    private static String createElasticTranscoderJob() throws Exception {
        
        // Setup the job input using the provided input key.
        JobInput input = new JobInput()
            .withKey(INPUT_KEY);
        
        // Setup the job output using the provided input key to generate an output key.
        List<CreateJobOutput> outputs = new ArrayList<CreateJobOutput>();
        CreateJobOutput output = new CreateJobOutput()
            .withKey(TranscoderSampleUtilities.inputKeyToOutputKey(INPUT_KEY))
            .withPresetId(PRESET_ID);
        outputs.add(output);
        
        // Create a job on the specified pipeline and return the job ID.
        CreateJobRequest createJobRequest = new CreateJobRequest()
            .withPipelineId(PIPELINE_ID)
            .withOutputKeyPrefix(OUTPUT_KEY_PREFIX)
            .withInput(input)
            .withOutputs(outputs);
        return amazonElasticTranscoder.createJob(createJobRequest).getJob().getId();
    }
    
    /**
     * Waits for the specified job to complete by adding a handler to the SQS
     * notification worker that is polling for status updates.  This method
     * will block until the specified job completes.
     * @param jobId
     * @param sqsQueueNotificationWorker
     * @throws InterruptedException
     */
    private static void waitForJobToComplete(final String jobId, SqsQueueNotificationWorker sqsQueueNotificationWorker) throws InterruptedException {
        
        // Create a handler that will wait for this specific job to complete.
        JobStatusNotificationHandler handler = new JobStatusNotificationHandler() {
            
            @Override
            public void handle(JobStatusNotification jobStatusNotification) {
                if (jobStatusNotification.getJobId().equals(jobId)) {
                    System.out.println(jobStatusNotification);
                    
                    if (jobStatusNotification.getState().isTerminalState()) {
                        synchronized(this) {
                            this.notifyAll();
                        }
                    }
                }
            }
        };
        sqsQueueNotificationWorker.addHandler(handler);
        
        // Wait for job to complete.
        synchronized(handler) {
            handler.wait();
        }
        
        // When job completes, shutdown the sqs notification worker.
        sqsQueueNotificationWorker.shutdown();
    }
}
// snippet-end:[elastictranscoder.java.job_status_notification_sample.import]
