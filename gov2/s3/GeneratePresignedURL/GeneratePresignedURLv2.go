// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.generate_presigned_url]
package main

import (
	"context"
	"flag"
	"fmt"

	v4 "github.com/aws/aws-sdk-go-v2/aws/signer/v4"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3PresignGetObjectAPI defines the interface for the PresignGetObject function.
// We use this interface to test the function using a mocked service.
type S3PresignGetObjectAPI interface {
	PresignGetObject(
		ctx context.Context,
		params *s3.GetObjectInput,
		optFns ...func(*s3.PresignOptions)) (*v4.PresignedHTTPRequest, error)
}

// GetPresignedURL retrieves a presigned URL for an Amazon S3 bucket object.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, the presigned URL for the object and nil.
//     Otherwise, nil and an error from the call to PresignGetObject.
func GetPresignedURL(c context.Context, api S3PresignGetObjectAPI, input *s3.GetObjectInput) (*v4.PresignedHTTPRequest, error) {
	return api.PresignGetObject(c, input)
}

func main() {
	bucket := flag.String("b", "", "The bucket")
	key := flag.String("k", "", "The object key")

	flag.Parse()

	if *bucket == "" || *key == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET) and object key (-k KEY)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.GetObjectInput{
		Bucket: bucket,
		Key:    key,
	}

	psClient := s3.NewPresignClient(client)

	resp, err := GetPresignedURL(context.TODO(), psClient, input)
	if err != nil {
		fmt.Println("Got an error retrieving pre-signed object:")
		fmt.Println(err)
		return
	}

	fmt.Println("The URL: ")
	fmt.Println(resp.URL)
}

// snippet-end:[s3.go-v2.generate_presigned_url]
