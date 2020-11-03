// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.copy_object]
package main

// snippet-start:[s3.gov2.copy_object.imports]
import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// snippet-end:[s3.go-v2.copy_object.imports]

// S3CopyObjectAPI defines the interface for the Amazon S3 CopyObject function.
// We use this interface to enable unit testing.
// snippet-start:[s3.gov2.copy_object.interface]
type S3CopyObjectAPI interface {
	CopyObject(ctx context.Context,
		params *s3.CopyObjectInput,
		optFns ...func(*s3.Options)) (*s3.CopyObjectOutput, error)
}

// snippet-end:[s3.go-v2.copy_object.interface]

// CopyItem copies an Amazon S3 item to another Amazon S3 bucket.
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a METHODOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to FUNCTION
func CopyItem(c context.Context, api S3CopyObjectAPI, input *s3.CopyObjectInput) (*s3.CopyObjectOutput, error) {
	// snippet-start:[s3.go-v2.copy_object.call]
	// Copy the item
	resp, err := api.CopyObject(c, input)
	// snippet-end:[s3.go-v2.copy_object.call]

	return resp, err
}

func main() {
	// snippet-start:[s3.go-v2.copy_object.args]
	sourceBucket := flag.String("f", "", "The bucket containing the object to copy")
	targetBucket := flag.String("t", "", "The bucket to which the object is copied")
	item := flag.String("i", "", "The object to copy")
	flag.Parse()

	if *sourceBucket == "" || *targetBucket == "" || *item == "" {
		fmt.Println("You must supply the bucket to copy from (-f BUCKET), to (-t BUCKET), and item to copy (-i ITEM")
		return
	}
	// snippet-end:[s3.go-v2.copy_object.args]

	// snippet-start:[s3.go-v2.copy_object.configclient]
	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)
	// snippet-end:[s3.go-v2.copy_object.configclient]

	input := &s3.CopyObjectInput{
		Bucket:     targetBucket,
		CopySource: sourceBucket,
		Key:        item,
	}

	_, err = CopyItem(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error copying item:")
		fmt.Println(err)
		return
	}

	// snippet-start:[s3.go-v2.copy_object.print]
	fmt.Println("Copied " + *item + " from " + *sourceBucket + " to " + *targetBucket)
	// snippet-end:[s3.go-v2.copy_object.print]
}

// snippet-end:[s3.go-v2.copy_object]
