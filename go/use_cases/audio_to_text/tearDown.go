/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package main

import (
	"log"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"
)

// deleteBucket deletes an S3 Bucket
func deleteBucket(sess *session.Session, bucketName string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Delete any bucket items
	iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
		Bucket: aws.String(bucketName),
	})

	// Traverse iterator deleting each object
	err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
	if err != nil {
		return err
	}

	// Delete the S3 Bucket
	_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: aws.String(bucketName),
	})
	if err != nil {
		return err
	}

	// Wait until bucket is deleted before finishing
	err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucketName),
	})
	if err != nil {
		return err
	}

	return nil
}

func tearDown() {
	// Get log filename from environment variable LOG_FILE
	logFile := os.Getenv("LOG_FILE")

	if logFile == "" {
		os.Exit(1)
	}

	f, err := os.OpenFile(logFile,
		os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Println(err)
	}
	defer f.Close()

	logger := log.New(f, "AUDIO_TO_TEXT", log.LstdFlags)

	// Get region from environment variable AWS_REGION
	region := os.Getenv("AWS_REGION")

	if region == "" {
		logger.Println("You must specify a region in the AWS_REGION env variable")
		os.Exit(1)
	}

	// Get input bucket name from INPUT_BUCKET env variable
	inputBucket := os.Getenv("INPUT_BUCKET")

	if inputBucket == "" {
		logger.Println("You must specify an input bucket name in the INPUT_BUCKET env variable")
		os.Exit(1)
	}

	// Get output bucket name from OUTPUT_BUCKET env variable
	outputBucket := os.Getenv("OUTPUT_BUCKET")

	if outputBucket == "" {
		logger.Println("You must specify an output bucket name in the OUTPUT_BUCKET env variable")
		os.Exit(1)
	}

	// Get Lambda function name from LAMBDA_FUNCTION env variable
	lambdaFunction := os.Getenv("LAMBDA_FUNCTION")

	if lambdaFunction == "" {
		logger.Println("You must specify a Lambda function name in the LAMBDA_FUNCTION env variable")
		os.Exit(1)
	}

	// Create a session, in the specified region, to use for all operations,
	// using default credentials
	sess := session.Must(session.NewSession(&aws.Config{
		Region: aws.String(region),
	}))

	err = deleteBucket(sess, inputBucket)
	if err != nil {
		logger.Println("Could not delete input bucket " + inputBucket)
		os.Exit(1)
	}

	err = deleteBucket(sess, outputBucket)
	if err != nil {
		logger.Println("Could not delete output bucket " + outputBucket)
		os.Exit(1)
	}
}
