// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Deletes the policy on a bucket. If the bucket doesn't exist, or there was
// and error an error message will be printed instead.
//
// Usage:
//
//	go run s3_delete_bucket_policy.go BUCKET_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("bucket name required\nUsage: %s bucket_name",
			filepath.Base(os.Args[0]))
	}
	bucket := os.Args[1]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Call S3 to delete the policy on the bucket.
	_, err = svc.DeleteBucketPolicy(&s3.DeleteBucketPolicyInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok && aerr.Code() == s3.ErrCodeNoSuchBucket {
			// Special error handling for the when the bucket doesn't
			// exists so we can give a more direct error message from the CLI.
			exitErrorf("Bucket %q does not exist", bucket)
		}
		exitErrorf("Unable to delete bucket %q policy, %v", bucket, err)
	}

	fmt.Printf("Successfully deleted the policy on bucket %q.\n", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
