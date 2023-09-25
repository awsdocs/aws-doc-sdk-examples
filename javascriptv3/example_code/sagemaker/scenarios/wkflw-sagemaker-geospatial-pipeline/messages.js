/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

export const MESSAGES = {
  greeting: `Welcome to the Amazon SageMaker pipeline example scenario. This example workflow will guide you through setting up and executing a SageMaker pipeline. The pipeline uses an AWS Lambda function and an Amazon Simple Queue Service (Amazon SQS) queue. It also runs a vector enrichment reverse geocode job to reverse geocode addresses in an input file and store the results in an export file.`,
  greetingHeader: "Welcome to Amazon SageMaker Pipelines.",
  creatingRole: "Creating IAM role: ${ROLE_NAME}.",
  roleCreated: "IAM role created: ${ROLE_NAME}.",
  continue: "Continue?",
  exit: "Exiting...",
  attachPolicy: 'Attaching IAM policy "${POLICY_NAME}" to role "${ROLE_NAME}".',
  policyAttached: "IAM Policy attached.",
  creatingFunction: "Creating Lambda function: ${FUNCTION_NAME}.",
  functionCreated: "Created Lambda function with name: ${FUNCTION_NAME}.",
  creatingSQSQueue: "Creating SQS queue: ${QUEUE_NAME}.",
  sqsQueueCreated: "Created SQS queue with name: ${QUEUE_NAME}.",
  configuringLambdaSQSEventSource:
    "Configuring ${LAMBDA_NAME} to receive events from ${QUEUE_NAME}.",
  lambdaSQSEventSourceConfigured:
    "Configured ${LAMBDA_NAME} to receive events from ${QUEUE_NAME}.",
  creatingPipeline: "Creating SageMaker pipeline: ${PIPELINE_NAME}.",
  pipelineCreated: "Created SageMaker pipeline with name: ${PIPELINE_NAME}.",
  creatingS3Bucket: "Creating S3 bucket: ${BUCKET_NAME}.",
  s3BucketCreated: "Created S3 bucket with name: ${BUCKET_NAME}.",
  uploadingInputData: "Uploading input data to S3 bucket: ${BUCKET_NAME}.",
  inputDataUploaded: "Uploaded input data to S3 bucket.",
  executePipeline:
    "Everything is ready. The next step is to execute the pipeline.",
  outputDelay:
    "It takes time for the generated output to be available. The S3 bucket will be checked for output every 10 seconds.",
  outputDataRetrieved: "A truncated list of the output:",
};
