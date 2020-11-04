// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.DeleteObject]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// S3DeleteObjectAPI defines the interface for the DeleteObject function.
// We use this interface to test the function using a mocked service.
type S3DeleteObjectAPI interface {
	DeleteObject(ctx context.Context,
		params *s3.DeleteObjectInput,
		optFns ...func(*s3.Options)) (*s3.DeleteObjectOutput, error)
}

// DeleteItem deletes an item from a bucket
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a DeleteObjectOutput object containing the result of the service call and nil
//     Otherwise, an error from the call to DeleteObject
func DeleteItem(c context.Context, api S3DeleteObjectAPI, input *s3.DeleteObjectInput) (*s3.DeleteObjectOutput, error) {
	result, err := api.DeleteObject(c, input)

	return result, err
}

func main() {
	bucket := flag.String("b", "", "The bucket from which the object is deleted")
	item := flag.String("i", "", "The object to delete")
	flag.Parse()

	if *bucket == "" || *item == "" {
		fmt.Println("You must supply the bucket (-b BUCKET), and item to delete (-i ITEM")
		return
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)

	input := &s3.DeleteObjectInput{
		Bucket: bucket,
		Key:    item,
	}

	_, err = DeleteItem(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error deleting item:")
		fmt.Println(err)
		return
	}

	fmt.Println("Deleted " + *item + " from " + *bucket)
}

// snippet-end:[s3.go-v2.DeleteObject]
