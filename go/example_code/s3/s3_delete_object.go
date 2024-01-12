// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Deletes the specified object in the specified S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//
//	go run s3_delete_object BUCKET_NAME OBJECT_NAME
func main() {
	if len(os.Args) != 3 {
		exitErrorf("Bucket and object name required\nUsage: %s bucket_name object_name",
			os.Args[0])
	}

	bucket := os.Args[1]
	obj := os.Args[2]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Delete the item
	_, err = svc.DeleteObject(&s3.DeleteObjectInput{Bucket: aws.String(bucket), Key: aws.String(obj)})
	if err != nil {
		exitErrorf("Unable to delete object %q from bucket %q, %v", obj, bucket, err)
	}

	err = svc.WaitUntilObjectNotExists(&s3.HeadObjectInput{
		Bucket: aws.String(bucket),
		Key:    aws.String(obj),
	})
	if err != nil {
		exitErrorf("Error occurred while waiting for object %q to be deleted, %v", obj, err)
	}

	fmt.Printf("Object %q successfully deleted\n", obj)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
