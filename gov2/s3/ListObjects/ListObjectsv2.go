// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.ListObjects]
package main

// snippet-start:[s3.go-v2.ListObjects.imports]
import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// snippet-end:[s3.go-v2.ListObjects.imports]

// S3ListObjectsAPI defines the interface for the ListObjectsV2 function.
// snippet-start:[s3.go-v2.ListObjects.interface]
type S3ListObjectsAPI interface {
	ListObjectsV2(ctx context.Context,
		params *s3.ListObjectsV2Input,
		optFns ...func(*s3.Options)) (*s3.ListObjectsV2Output, error)
}

// snippet-end:[s3.go-v2.ListObjects.interface]

// GetObjects retrieves the objects in an Amazon S3 bucket
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a ListObjectsV2Output object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to ListObjectsV2
func GetObjects(c context.Context, api S3ListObjectsAPI, input *s3.ListObjectsV2Input) (*s3.ListObjectsV2Output, error) {
	// Get the list of items
	// snippet-start:[s3.go-v2.ListObjects.call]
	resp, err := api.ListObjectsV2(c, input)
	// snippet-end:[s3.go-v2.ListObjects.call]

	return resp, err
}

// Lists the items in the specified S3 bucket
//
// Usage:
//    go run s3_list_objects.go BUCKET_NAME
func main() {
	// snippet-start:[s3.go-v2.ListObjects.args]
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply the name of a bucket (-b BUCKET)")
		return
	}
	// snippet-end:[s3.go-v2.ListObjects.args]

	// snippet-start:[s3.go-v2.ListObjects.configclient]
	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)
	// snippet-end:[s3.go-v2.ListObjects.configclient]

	input := &s3.ListObjectsV2Input{
		Bucket: bucket,
	}

	resp, err := GetObjects(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got error retrieving list of objects:")
		fmt.Println(err)
		return
	}

	// snippet-start:[s3.go-v2.ListObjects.print]
	fmt.Println("Objects in " + *bucket + ":")

	for _, item := range resp.Contents {
		fmt.Println("Name:          ", *item.Key)
		fmt.Println("Last modified: ", *item.LastModified)
		fmt.Println("Size:          ", *item.Size)
		fmt.Println("Storage class: ", string(item.StorageClass))
		fmt.Println("")
	}

	fmt.Println("Found", len(resp.Contents), "items in bucket", *bucket)
	fmt.Println("")
	// snippet-end:[s3.go-v2.ListObjects.print]
}

// snippet-end:[s3.go-v2.ListObjects]
