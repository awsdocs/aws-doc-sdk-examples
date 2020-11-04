// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.DeleteBucket]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3DeleteBucketAPI defines the interface for the DeleteBucket function.
// We use this interface to test the function using a mocked service.
type S3DeleteBucketAPI interface {
	DeleteBucket(ctx context.Context,
		params *s3.DeleteBucketInput,
		optFns ...func(*s3.Options)) (*s3.DeleteBucketOutput, error)
}

// RemoveBucket deletes a bucket
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a DeleteBucketOutput object containing the result of the service call and nil
//     Otherwise, an error from the call to CreateBucket
func RemoveBucket(c context.Context, api S3DeleteBucketAPI, input *s3.DeleteBucketInput) (*s3.DeleteBucketOutput, error) {
	result, err := api.DeleteBucket(c, input)

	return result, err
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

	input := &s3.DeleteBucketInput{
		Bucket: bucket,
	}

	_, err = RemoveBucket(context.Background(), client, input)
	if err != nil {
		fmt.Println("Could not delete bucket " + *bucket)
	}
}

// snippet-end:[s3.go-v2.DeleteBucket]
