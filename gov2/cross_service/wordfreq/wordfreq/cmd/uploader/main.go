// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"os"
	"time"

	wordfreq "example.aws/go-v2/examples/cross_service/wordfreq/service/shared"
	"github.com/aws/aws-sdk-go-v2/aws"
	awsConfig "github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials/stscreds"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/google/uuid"
	"go.uber.org/zap"
)

// Uploads a file to S3 so it can be processed by the Word Frequency service.
// If a "WORKER_RESULT_QUEUE_URL" environment variable is provided the upload
// client will wait for the job to processed, and print the results to the console.
//

var bucket string
var queueURL string
var submissionRole string
var log *zap.Logger

func init() {
	envBucketName := os.Getenv("UPLOAD_BUCKET_NAME")
	envQueueUrl := os.Getenv("RESULT_QUEUE_URL")
	envSubRole := os.Getenv("SUBMITTER_ROLE")
	flag.StringVar(&bucket, "bucket", envBucketName, "name of the bucket to upload to")
	flag.StringVar(&queueURL, "queueUrl", envQueueUrl, "Queue URL")
	flag.StringVar(&submissionRole, "submitterRole", envSubRole, "Role to take on when making submissions")
	tlog, err := zap.NewDevelopment()
	if err == nil {
		log = tlog
	} else {
		fmt.Println("FATAL: couldn't start logger.")
		os.Exit(1)
	}
}

func usage() {
	fmt.Printf("Usage: %v [options] filename \noptions:\n", os.Args[0])

	flag.Usage()

	os.Exit(1)
}

func main() {
	defer log.Sync()

	var filename string

	flag.Parse()

	if flag.NArg() != 1 {
		// We only want one
		fmt.Printf("One argument expected, got %v\n", flag.NArg())

		usage()
	}

	filename = flag.Arg(0)

	file, err := os.Open(filename)
	if err != nil {
		log.Fatal("Failed to open source file", zap.String("filename", filename))
	}
	defer file.Close()

	// we need to make a unique name. Two files with the same content could overlap.

	uploadFilename, err := uuid.NewRandom()

	if err != nil {
		log.Fatal("Couldn't generate filename", zap.Error(err))
	}

	// Create a session which contains the default configurations for the SDK.
	// Use the session to create the service clients to make API calls to AWS.

	config, err := awsConfig.LoadDefaultConfig(context.TODO())

	if err != nil {
		log.Fatal("Couldn't load default config", zap.Error(err))
	}

	config.Region = "us-west-2"

	// check if we need to assume the submission role

	log.Debug("checking for role credentials", zap.String("subRole", submissionRole))
	if submissionRole != "" {
		log.Info("Requesting config from STS", zap.String("rolearn", submissionRole))
		stsClient := sts.NewFromConfig(config)

		_, err := stsClient.AssumeRole(context.TODO(), &sts.AssumeRoleInput{
			RoleSessionName: aws.String("uploader"),
			RoleArn:         &submissionRole,
		})

		if err != nil {
			log.Fatal("Couldn't assume the role for uploading", zap.String("roleArn", submissionRole),
				zap.Error(err))
		} else {
			log.Info("Creating new role provider for role", zap.String("roleArn", submissionRole))
			ourCreds := stscreds.NewAssumeRoleProvider(stsClient, submissionRole)

			tmpCreds, err := ourCreds.Retrieve(context.TODO())
			if err != nil {
				log.Fatal("Failed to retrieve credentials for role",
					zap.String("roleArn", submissionRole),
					zap.Error(err),
				)
			} else {
				log.Debug("AssumeRoleProvider can get credentials!", zap.String("tmpAccessKeyID", tmpCreds.AccessKeyID))
				config.Credentials = ourCreds
			}
		}
	}

	svc := s3.NewFromConfig(config)

	log.Info("Uploading file to s3",
		zap.String("bucket", bucket),
		zap.String("key", uploadFilename.String()),
	)

	reader, err := os.Open(filename)

	if err != nil {
		log.Fatal("Couldn't open file for reading", zap.Error(err))
	}

	_, err = svc.PutObject(context.TODO(), &s3.PutObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(uploadFilename.String()),
		Body:   reader,
	})

	if err != nil {
		fmt.Println("error", err)
		os.Exit(1)
	}

	log.Info("Uploaded to S3, awaiting results",
		zap.String("bucket", bucket),
		zap.String("key", uploadFilename.String()),
	)

	sqsClient := sqs.NewFromConfig(config)
	waitForResult(sqsClient, bucket, uploadFilename.String(), queueURL)

}

// waitForResult waits for the job to be processed and the job result to be added
// to the job result SQS queue.  This will pool the SQS queue for job results until
// a job result matches the file it uploaded. When a match is found the job result
// will also be deleted from the queue, and its status written to the console.
// If the job result doesn't match the file uploaded by this client, the message
// will be ignored, so another client could received it.
func waitForResult(svc *sqs.Client, bucket, filename, resultQueueURL string) {

	for {
		resp, err := svc.ReceiveMessage(context.TODO(), &sqs.ReceiveMessageInput{
			QueueUrl:          aws.String(resultQueueURL),
			VisibilityTimeout: 0,
			WaitTimeSeconds:   20,
		})

		if err != nil {
			log.Info("Failed to receive message", zap.Error(err))
			time.Sleep(20 * time.Second)
			continue
		}

		for _, message := range resp.Messages {
			result := &wordfreq.JobResult{}
			if err := json.Unmarshal([]byte(*message.Body), result); err != nil {
				log.Fatal("Couldn't unmarshall JSON response",
					zap.String("responseRaw", *message.Body),
					zap.Error(err),
				)
			}

			if result.FileKey == filename {
				// Delete the item out of the queue
				_, err := svc.DeleteMessage(context.TODO(), &sqs.DeleteMessageInput{
					QueueUrl:      aws.String(queueURL),
					ReceiptHandle: message.ReceiptHandle,
				})

				if err != nil {
					log.Error("Tried to, failed to clean up queue result.")
				}

				log.Debug("Got response back:", zap.String("result", result.StatusMessage))
				printResult(result)
				return

			} else {
				continue
			}
		}
	}
}

// printResult prints the job results to the console.
func printResult(result *wordfreq.JobResult) {
	fmt.Printf("Job Results completed in %s for %s\n",
		result.Duration.String(), result.JobID)
	if result.Status == wordfreq.JobCompleteFailure {
		fmt.Println("Failed:", result.StatusMessage)
		return
	}

	fmt.Println("Top Words:")
	for _, w := range result.Words {
		format := "- %s\t%d\n"
		if len(w.Word) <= 5 {
			format = "- %s\t\t%d\n"
		}
		fmt.Printf(format, w.Word, w.Count)
	}
}
