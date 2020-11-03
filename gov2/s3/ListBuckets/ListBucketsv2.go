// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[s3.go-v2.list_buckets]
package main

// snippet-start:[s3.go-v2.list_buckets.imports]
import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

// snippet-end:[s3.go-v2.list_buckets.imports]

// S3ListBucketsAPI defines the interface for the ListBuckets function.
// snippet-start:[s3.go-v2.ListBuckets.interface]
type S3ListBucketsAPI interface {
	ListBuckets(ctx context.Context,
		params *s3.ListBucketsInput,
		optFns ...func(*s3.Options)) (*s3.ListBucketsOutput, error)
}

// snippet-end:[s3.go-v2.ListBuckets.interface]

// GetAllBuckets retrieves a list of your Amazon S3 buckets.
// Inputs:
//     c is the context of the method call, which includes the Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If success, a ListBucketsOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListBuckets.

// GetAllBuckets retrieves a list of all buckets.
// Inputs:
//     cfg is the current cfgion, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of buckets and nil
//     Otherwise, nil and an error from the call to ListBuckets
func GetAllBuckets(c context.Context, api S3ListBucketsAPI, input *s3.ListBucketsInput) (*s3.ListBucketsOutput, error) {
	// snippet-start:[s3.go-v2.list_buckets.imports.call]
	result, err := api.ListBuckets(c, input)
	// snippet-end:[s3.go-v2.list_buckets.imports.call]
	if err != nil {
		return nil, err
	}

	return result, nil
}

func main() {
	// snippet-start:[s3.go-v2.ListBuckets.configclient]
	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := s3.NewFromConfig(cfg)
	// snippet-end:[s3.go-v2.ListBuckets.configclient]

	input := &s3.ListBucketsInput{}

	result, err := GetAllBuckets(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving buckets:")
		fmt.Println(err)
		return
	}

	// snippet-start:[s3.go-v2.list_buckets.imports.print]
	fmt.Println("Buckets:")

	for _, bucket := range result.Buckets {
		fmt.Println(*bucket.Name + ": " + bucket.CreationDate.Format("2006-01-02 15:04:05 Monday"))
	}
	// snippet-end:[s3.go-v2.list_buckets.imports.print]
}

// snippet-end:[s3.go-v2.list_buckets]
