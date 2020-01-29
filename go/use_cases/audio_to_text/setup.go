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
	"errors"
	"io/ioutil"
	"log"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
	"github.com/aws/aws-sdk-go/service/lambda"
	"github.com/aws/aws-sdk-go/service/s3"
)

// createBucket creates an S3 Bucket in the specified region
func createBucket(sess *session.Session, bucketName string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Create the S3 Bucket
	_, err := svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: aws.String(bucketName),
	})
	if err != nil {
		return err
	}

	// Wait until bucket is created before finishing
	err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucketName),
	})
	if err != nil {
		return err
	}

	return nil
}

func createRole(sess *session.Session) (*iam.Role, error) {
	svc := iam.New(session.New())

	// Create role with AWSLambdaExecute managed policy
	policyDoc := "{ " +
		"}"

	crInput := &iam.CreateRoleInput{
		AssumeRolePolicyDocument: aws.String(policyDoc),
		Path:     aws.String("/"),
		RoleName: aws.String("LambdaExecution"),
	}

	result, err := svc.CreateRole(crInput)
	if err != nil {
		return nil, errors.New("Could not create role")
	}
	role := result.Role

	arpInput := &iam.AttachRolePolicyInput{
		PolicyArn: aws.String("arn:aws:iam::aws:policy/AWSLambdaExecute"),
		RoleName:  aws.String("LambdaExecution"),
	}

	_, err = svc.AttachRolePolicy(arpInput)
	if err != nil {
		return nil, errors.New("Could not attach AWSLambdaExecute managed policy to role")
	}

	return role, err
}

func createLambdaFunction(sess *session.Session, bucketName string, lambdaZipFileName string) error {
	contents, err := ioutil.ReadFile(lambdaZipFileName)
	if err != nil {
		return errors.New("Could not read Lambda Zip file " + lambdaZipFileName)
	}

	svc := lambda.New(sess)

	createCode := &lambda.FunctionCode{
		S3Bucket:        aws.String(bucketName),
		S3Key:           aws.String(lambdaZipFileName),
		S3ObjectVersion: aws.String(""),
		ZipFile:         contents,
	}

	role, err := createRole(sess)
	if err != nil {
		return err
	}

	createArgs := &lambda.CreateFunctionInput{
		Code:         createCode,
		FunctionName: aws.String("ConvertAudioToText"),
		Handler:      aws.String("HandleRequest"),
		Role:         role.Arn,
		Runtime:      aws.String("go1.x"),
	}

	_, err = svc.CreateFunction(createArgs)
	if err != nil {
		return err
	}

	return nil
}

func setLambdaFunction(sess *session.Session, functionName string, bucketArn string) error {
	svc := lambda.New(sess)

	// Allow Lambda input from bucket with specified ARN
	permArgs := &lambda.AddPermissionInput{
		Action:       aws.String("lambda:InvokeFunction"),
		FunctionName: aws.String(functionName),
		Principal:    aws.String("s3.amazonaws.com"),
		SourceArn:    aws.String(bucketArn),
		StatementId:  aws.String("lambda_s3_notification"),
	}

	_, err := svc.AddPermission(permArgs)
	if err != nil {
		return err
	}

	return nil
}

func setup() {
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

	// Get bucket name to hold Lambda function from LAMBDA_BUCKET env variable
	lambdaBucket := os.Getenv("LAMBDA_BUCKET")

	if lambdaBucket == "" {
		logger.Println("You must specify a bucket name to hold Lambda code in the LAMBDA_BUCKET env variable")
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

	// Create input bucket
	err = createBucket(sess, inputBucket)
	if err != nil {
		logger.Println("Could not create input bucket " + inputBucket)
		os.Exit(1)
	}

	// Create output bucket
	err = createBucket(sess, outputBucket)
	if err != nil {
		logger.Println("Could not create output bucket " + outputBucket)
		os.Exit(1)
	}

	// Create lambda bucket
	err = createBucket(sess, lambdaBucket)
	if err != nil {
		logger.Println("Could not create Lambda bucket " + lambdaBucket)
		os.Exit(1)
	}

	err = createLambdaFunction(sess, lambdaBucket, "main.zip")

	err = setLambdaFunction(sess, lambdaFunction, "arn:aws:s3:::"+inputBucket)
	if err != nil {
		logger.Println("Could not enable input bucket " + inputBucket + " to notify Lambda function " + lambdaFunction)
		os.Exit(1)
	}
}
