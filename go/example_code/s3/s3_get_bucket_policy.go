// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Prints the policy for a bucket. If the bucket doesn't exist, or there was
// and error an error message will be printed instead.
//
// Usage:
//
//	go run s3_get_bucket_policy.go BUCKET_NAME
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

	// Call S3 to retrieve the policy for the selected bucket.
	result, err := svc.GetBucketPolicy(&s3.GetBucketPolicyInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		// Special error handling for the when the bucket doesn't
		// exists so we can give a more direct error message from the CLI.
		if aerr, ok := err.(awserr.Error); ok {
			switch aerr.Code() {
			case s3.ErrCodeNoSuchBucket:
				exitErrorf("Bucket %q does not exist.", bucket)
			case "NoSuchBucketPolicy":
				exitErrorf("Bucket %q does not have a policy.", bucket)
			}
		}
		exitErrorf("Unable to get bucket %q policy, %v.", bucket, err)
	}

	out := bytes.Buffer{}
	policyStr := aws.StringValue(result.Policy)
	if err := json.Indent(&out, []byte(policyStr), "", "  "); err != nil {
		exitErrorf("Failed to pretty print bucket policy, %v.", err)
	}

	fmt.Printf("%q's Bucket Policy:\n", bucket)
	fmt.Println(out.String())
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
