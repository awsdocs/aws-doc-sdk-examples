// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.make_bucket_public]
package main

// snippet-start:[s3.go.make_bucket_public.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.make_bucket_public.imports]

// SetBucketPublic give everyone access to a bucket
// Inputs:
//
//	sess is the current session, which provides configuration for the SDK's service clients
//	bucket is the name of the bucket
//
// Output:
//
//	If success, nil
//	Otherwise, an error from the call to PutBucketAcl
func SetBucketPublic(sess *session.Session, bucket *string) error {
	// snippet-start:[s3.go.make_bucket_public.call]
	svc := s3.New(sess)

	params := &s3.PutBucketAclInput{
		Bucket: bucket,
		ACL:    aws.String("public-read"),
	}

	_, err := svc.PutBucketAcl(params)
	// snippet-end:[s3.go.make_bucket_public.call]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	// snippet-start:[s3.go.make_bucket_public.args]
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply the name of a bucket (-b BUCKET)")
		return
	}
	// snippet-end:[s3.go.make_bucket_public.args]

	// snippet-start:[s3.go.make_bucket_public.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[s3.go.make_bucket_public.session]

	err := SetBucketPublic(sess, bucket)
	if err != nil {
		fmt.Println("Could not set public access to bucket " + *bucket)
		return
	}

	fmt.Println("Bucket " + *bucket + " is now public")
}

// snippet-end:[s3.go.make_bucket_public]
