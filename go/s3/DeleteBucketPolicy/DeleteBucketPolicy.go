// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.delete_bucket_policy]
package main

// snippet-start:[s3.go.delete_bucket_policy.imports]
import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.delete_bucket_policy.imports]

// RemoveBucketPolicy removes the policy for a bucket
// Inputs:
//
//	sess is the current session, which provides configuration for the SDK's service clients
//	bucket is the name of the bucket
//
// Output:
//
//	If success, nil
//	Otherwise, an error from the call to DeleteBucketPolicy
func RemoveBucketPolicy(sess *session.Session, bucket *string) error {
	// snippet-start:[s3.go.delete_bucket_policy.call]
	svc := s3.New(sess)

	_, err := svc.DeleteBucketPolicy(&s3.DeleteBucketPolicyInput{
		Bucket: bucket,
	})
	// snippet-end:[s3.go.delete_bucket_policy.call]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	// snippet-start:[s3.go.delete_bucket_policy.args]
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}
	// snippet-end:[s3.go.delete_bucket_policy.args]

	// snippet-start:[s3.go.delete_bucket_policy.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[s3.go.delete_bucket_policy.session]

	err := RemoveBucketPolicy(sess, bucket)
	if err != nil {
		fmt.Println("Got an error removing the bucket policy:")
		fmt.Println(err)
		return
	}

	// snippet-start:[s3.go.delete_bucket_policy.print]
	fmt.Println("Removed the bucket policy")
	// snippet-end:[s3.go.delete_bucket_policy.print]
}

// snippet-end:[s3.go.delete_bucket_policy]
