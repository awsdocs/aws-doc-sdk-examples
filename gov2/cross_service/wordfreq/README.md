# Word Frequency Counting example

# Purpose

This example demonstrates using multiple services together to create a word frequency counting service. It demonstrates:

* Using the AWS Cloud Development Kit (CDK) for Go to deploy and manage an environment
* Using Amazon Simple Storage Service (S3) event notifications to respond to events
* Using Amazon Simple Queue Service (Amazon SQS) to deliver messages to endpoints
* Using Amazon Elastic Container Service (Amazon ECS) for AWS Fargate to horizontally scale processing based on load

# Requirements

In order to run this example, you must first

* Confirm the AWS CDK is installed with `cdk --version` and that `cdk bootstrap` has been run with your account; for information on getting started with the CDK, see [Getting Started](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html) in the CDK Developer Guide.
* Confirm Go 1.16 or later is installed with `go version`; for information on installing Go, see [the Golang documentation](https://go.dev/doc/install).
* Confirm Docker is running using `docker container ls`; for information on installing Docker, see [the Docker documentation](https://docs.docker.com/get-docker/).

# Running the example

## Deploying the back-end

To deploy the processing infrastructure, you use `cdk deploy`. This will

* Build the Docker container used to run the processing logic
* Deploy the required AWS infrastructure
* Push the locally built Docker container to a private Amazon Elastic COntainer Registry (AWS ECR) registry
* Create the appropriate roles and AWS Identity and Access Management (IAM) policies

The CDK deployment will yield a handful of outputs:

```
WordfreqStack: creating CloudFormation changeset...

 ✅  WordfreqStack

Outputs:
WordfreqStack.InputQueueUrl = https://sqs.us-west-2.amazonaws.com/112233445566/WordfreqStack-WorkQueue07846ecd-60a6-4bc6-baf1-381fd46e820d
WordfreqStack.bucketName = wordfreqstack-contentbucket-EXAMPLE1-123456789
WordfreqStack.resultQueueUrl = https://sqs.us-west-2.amazonaws.com/112233445566/WordfreqStack-resultQueue37168752-17W51ZTIPQV5X
WordfreqStack.submitRoleArn = arn:aws:iam::112233445566:role/WordfreqStack-QueueSubmitterRole123EXAM-PLE456789
```

You will need the following for the frontend:

* WordfreqStack.bucketName - This is the Amazon S3 Bucket that you will upload files to
* WordfreqStack.resultQueueUrl - This is the queue the frontend listens to for notifications of return work
* WordfreqStack.submitRoleArn - This is the role that you must use to submit items to and use to get results.

## Using the frontend

You must build the uploader first; This is a Go application that uses the AWS SDK for Go v2. A Makefile is included, however you are not required to use it.

```
$ cd wordfreq
$ make clean uploader

 - or -

$ go mod download
$ go build ./cmd/uploader/ -o .
```

An executable, `uploader` (or `uploader.exe`) will be generated as a result. 

This requires the outputs from the `cdk deploy` command. 


```
$ ./uploader -bucket "wordfreqstack-contentbucket-EXAMPLE1-123456789" \
   -queueUrl "https://sqs.us-west-2.amazonaws.com/112233445566/WordfreqStack-resultQueue37168752-17W51ZTIPQV5X" \
   -submitterRole "arn:aws:iam::112233445566:role/WordfreqStack-QueueSubmitterRole123EXAM-PLE456789" \
   ../gutenberg2.txt
```

you'll see output similar to

```
2021-08-30T13:28:16.825-0700	DEBUG	uploader/main.go:99	checking for role credentials	{"subRole": "arn:aws:iam::112233445566:role/WordfreqStack-QueueSubmitterRole123EXAM-PLE456789"}
2021-08-30T13:28:16.826-0700	INFO	uploader/main.go:101	Requesting config from STS	{"rolearn": "arn:aws:iam::112233445566:role/WordfreqStack-QueueSubmitterRole123EXAM-PLE456789"}
2021-08-30T13:28:17.056-0700	INFO	uploader/main.go:113	Creating new role provider for role	{"roleArn": "arn:aws:iam::112233445566:role/WordfreqStack-QueueSubmitterRole123EXAM-PLE456789"}
2021-08-30T13:28:17.078-0700	DEBUG	uploader/main.go:123	AssumeRoleProvider can get credentials!	{"tmpAccessKeyID": "AKIAIOSFODNN7EXAMPLE"}
2021-08-30T13:28:17.078-0700	INFO	uploader/main.go:131	Uploading file to s3	{"bucket": "wordfreqstack-contentbucket-EXAMPLE1-123456789", "key": "08458d49-5c20-4185-a75a-d3ca7eeffe23"}
2021-08-30T13:28:17.423-0700	INFO	uploader/main.go:153	Uploaded to S3, awaiting results	{"bucket": "wordfreqstack-contentbucket-EXAMPLE1-123456789", "key": "08458d49-5c20-4185-a75a-d3ca7eeffe23"}
2021-08-30T13:28:19.376-0700	DEBUG	uploader/main.go:204	Got response back:	{"result": "Success"}
Job Results completed in 16.143640947s for avsdmgqjsdts
Top Words:
- which		324
- paper		234
- light		212
- plate		147
- silver	117
- picture	114
- solution	113
- should	105
- water		98
- project	91
```

## What's happening behind the scenes:

1. The uploader generates a unique random key for its upload, and uploads the file to Amazon S3
2. The Amazon S3 bucket, upon the PUT operation completing, places the item onto the Amazon SQS queue
3. The Amazon SQS queue delivers the message to the running Fargate container
4. The Fargate container then processes the message, deletes it, and in turn sends a message to the output queue
5. The uploader receives the message on the output queue and deletes it.
6. Any file that fails to process correctly is left for 14 days.

Once the Fargate container receives a message, each object processing step is tracked by another unique ID, a Snowflake. Snowflakes are
time-stamped unique values pioneered by Twitter. They serve here as a way to track the unique requests that come in in the case of a
duplicate S3 object key. 

## Limitations

If two clients happen to use the same S3 upload key, their message will be processed in an unpredictable way. At least one uploader will
receive a success message while the other is likely to get a failure message, since the process deletes items out of S3. Which client will
get which message is undefined and non-deterministic. If you wish to guarantee that no two clients produce the same key, consider using
a web service that produces pre-signed S3 URLs for file upload. 

# About the AWS CDK for Go

**NOTICE**: Go support is still in Developer Preview. This implies that APIs may
change while we address early feedback from the community. We would love to hear
about your experience through GitHub issues.

## Useful commands

 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk synth`       emits the synthesized CloudFormation template
