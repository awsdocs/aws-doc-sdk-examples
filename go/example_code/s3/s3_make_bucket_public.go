// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"

	"fmt"
	"os"
)

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}

// Gives everyone read-only access to BUCKET.
//
// Usage:
//
//	go run s3_make_bucket_public.go BUCKET
func main() {
	if len(os.Args) < 2 {
		exitErrorf("Bucket name required.\nUsage: go run", os.Args[0], "BUCKET")
	}

	bucket := os.Args[1]

	// private | public-read | public-read-write | authenticated-read
	// See https://docs.aws.amazon.com/AmazonS3/latest/dev/acl-overview.html#CannedACL for details
	acl := "public-read"

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and region from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create S3 service client
	svc := s3.New(sess)

	params := &s3.PutBucketAclInput{
		Bucket: &bucket,
		ACL:    &acl,
	}

	// Set bucket ACL
	_, err := svc.PutBucketAcl(params)
	if err != nil {
		exitErrorf(err.Error())
	}

	fmt.Println("Bucket " + bucket + " is now public")
}
