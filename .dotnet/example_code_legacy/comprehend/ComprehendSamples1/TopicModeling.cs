// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[comprehend.dotNET.TopicModeling]
using System;
using Amazon;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace ComprehendSamples1
{
    class TopicModeling
    {
        // Helper method for printing properties
        static private void PrintJobProperties(TopicsDetectionJobProperties props)
        {
            Console.WriteLine("JobId: {0}, JobName: {1}, JobStatus: {2}, NumberOfTopics: {3}\nInputS3Uri: {4}, InputFormat: {5}, OutputS3Uri: {6}",
                props.JobId, props.JobName, props.JobStatus, props.NumberOfTopics,
                props.InputDataConfig.S3Uri, props.InputDataConfig.InputFormat, props.OutputDataConfig.S3Uri);
        }

        public static void Sample()
        {
            var comprehendClient = new AmazonComprehendClient(Amazon.RegionEndpoint.USWest2);

            String inputS3Uri = "s3://input bucket/input path";
            InputFormat inputDocFormat = InputFormat.ONE_DOC_PER_FILE;
            String outputS3Uri = "s3://output bucket/output path";
            String dataAccessRoleArn = "arn:aws:iam::account ID:role/data access role";
            int numberOfTopics = 10;

            var startTopicsDetectionJobRequest = new StartTopicsDetectionJobRequest()
            {
                InputDataConfig = new InputDataConfig()
                {
                    S3Uri = inputS3Uri,
                    InputFormat = inputDocFormat
                },
                OutputDataConfig = new OutputDataConfig()
                {
                    S3Uri = outputS3Uri
                },
                DataAccessRoleArn = dataAccessRoleArn,
                NumberOfTopics = numberOfTopics
            };

            var startTopicsDetectionJobResponse = comprehendClient.StartTopicsDetectionJob(startTopicsDetectionJobRequest);

            var jobId = startTopicsDetectionJobResponse.JobId;
            Console.WriteLine("JobId: " + jobId);

            var describeTopicsDetectionJobRequest = new DescribeTopicsDetectionJobRequest()
            {
                JobId = jobId
            };

            var describeTopicsDetectionJobResponse = comprehendClient.DescribeTopicsDetectionJob(describeTopicsDetectionJobRequest);
            PrintJobProperties(describeTopicsDetectionJobResponse.TopicsDetectionJobProperties);

            var listTopicsDetectionJobsResponse = comprehendClient.ListTopicsDetectionJobs(new ListTopicsDetectionJobsRequest());
            foreach (var props in listTopicsDetectionJobsResponse.TopicsDetectionJobPropertiesList)
                PrintJobProperties(props);
        }
    }
}
// snippet-end:[comprehend.dotNET.TopicModeling]