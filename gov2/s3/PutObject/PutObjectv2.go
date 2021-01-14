// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.PutObject]
package main

import (
	"context"
	"flag"
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3PutObjectAPI defines the interface for the PutObject function.
// We use this interface to test the function using a mocked service.
type S3PutObjectAPI interface {
	PutObject(ctx context.Context,
		params *s3.PutObjectInput,
		optFns ...func(*s3.Options)) (*s3.PutObjectOutput, error)
}

// PutFile uploads a file to an Amazon Simple Storage Service (Amazon S3) bucket
// Inputs:
//     c is the context of the method call, which includes the AWS Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a PutObjectOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to PutObject
func PutFile(c context.Context, api S3PutObjectAPI, input *s3.PutObjectInput) (*s3.PutObjectOutput, error) {
	resp, err := api.PutObject(c, input)

	return resp, err
}

func main() {
	bucket := flag.String("b", "", "The bucket to upload the file to")
	filename := flag.String("f", "", "The file to upload")
	flag.Parse()

	if *bucket == "" || *filename == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET) and file name (-f FILE)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	file, err := os.Open(*filename)

	if err != nil {
		fmt.Println("Unable to open file " + *filename)
		return
	}

	defer file.Close()

	input := &s3.PutObjectInput{
		Bucket: bucket,
		Key:    filename,
		Body:   file,
	}

	_, err = PutFile(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got error uploading file:")
		fmt.Println(err)
		return
	}
}

// snippet-end:[s3.go-v2.PutObject]
