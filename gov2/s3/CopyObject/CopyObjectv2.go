// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.CopyObject]
package main

import (
	"context"
	"flag"
	"fmt"
	"net/url"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3CopyObjectAPI defines the interface for the Amazon Simple Storage Service (Amazon S3) CopyObject function.
// We use this interface to enable unit testing.
type S3CopyObjectAPI interface {
	CopyObject(ctx context.Context,
		params *s3.CopyObjectInput,
		optFns ...func(*s3.Options)) (*s3.CopyObjectOutput, error)
}

// CopyItem copies an Amazon S3 object from one bucket to another.
// Inputs:
//     c is the context of the method call, which includes the AWS Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a CopyObjectOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CopyObject.
func CopyItem(c context.Context, api S3CopyObjectAPI, input *s3.CopyObjectInput) (*s3.CopyObjectOutput, error) {
	return api.CopyObject(c, input)
}

func main() {
	sourceBucket := flag.String("s", "", "The source bucket containing the object to copy")
	destinationBucket := flag.String("d", "", "The destination bucket to which the object is copied")
	objectName := flag.String("o", "", "The object to copy")
	flag.Parse()

	if *sourceBucket == "" || *destinationBucket == "" || *objectName == "" {
		fmt.Println("You must supply the bucket to copy from (-s BUCKET), to (-td BUCKET), and object to copy (-o OBJECT")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.CopyObjectInput{
		Bucket:     aws.String(url.PathEscape(*sourceBucket)),
		CopySource: destinationBucket,
		Key:        objectName,
	}

	_, err = CopyItem(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error copying item:")
		fmt.Println(err)
		return
	}

	fmt.Println("Copied " + *objectName + " from " + *sourceBucket + " to " + *destinationBucket)
}

// snippet-end:[s3.go-v2.CopyObject]
