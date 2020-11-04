// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.CreateBucket]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3CreateBucketAPI defines the interface for the CreateBucket function.
// We use this interface to test the function using a mocked service.
type S3CreateBucketAPI interface {
	CreateBucket(ctx context.Context,
		params *s3.CreateBucketInput,
		optFns ...func(*s3.Options)) (*s3.CreateBucketOutput, error)
}

// MakeBucket creates an Amazon S3 bucket
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a CreateBucketOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to CreateBucket
func MakeBucket(c context.Context, api S3CreateBucketAPI, input *s3.CreateBucketInput) (*s3.CreateBucketOutput, error) {
	resp, err := api.CreateBucket(c, input)

	return resp, err
}

func main() {
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.CreateBucketInput{
		Bucket: bucket,
	}

	_, err = MakeBucket(context.Background(), client, input)
	if err != nil {
		fmt.Println("Could not create bucket " + *bucket)
	}
}

// snippet-end:[s3.go-v2.CreateBucket]
