// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"net/url"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Copies the item in the bucket to another bucket.
//
// Usage:
//
//	go run s3_copy_object.go BUCKET ITEM OTHER_BUCKET
func main() {
	if len(os.Args) != 4 {
		exitErrorf("Bucket, item, and other bucket names required\nUsage: go run s3_copy_object bucket item other-bucket")
	}

	bucket := os.Args[1]
	item := os.Args[2]
	other := os.Args[3]

	source := bucket + "/" + item

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Copy the item
	_, err = svc.CopyObject(&s3.CopyObjectInput{Bucket: aws.String(other),
		CopySource: aws.String(url.QueryEscape(source)), Key: aws.String(item)})
	if err != nil {
		exitErrorf("Unable to copy item from bucket %q to bucket %q, %v", bucket, other, err)
	}

	// Wait to see if the item got copied
	err = svc.WaitUntilObjectExists(&s3.HeadObjectInput{Bucket: aws.String(other), Key: aws.String(item)})
	if err != nil {
		exitErrorf("Error occurred while waiting for item %q to be copied to bucket %q, %v", bucket, item, other, err)
	}

	fmt.Printf("Item %q successfully copied from bucket %q to bucket %q\n", item, bucket, other)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
