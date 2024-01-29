// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[s3.go.set_bucket_policy]
package main

// snippet-start:[s3.go.set_bucket_policy.imports]
import (
	"encoding/json"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.set_bucket_policy.imports]

// SetBucketPolicy applies the policy to a bucket
// Inputs:
//
//	sess is the current session, which provides configuration for the SDK's service clients
//	bucket is the name of the bucket
//
// Output:
//
//	If success, nil
//	Otherwise, an error from the call to PutBucketPolicy
func SetBucketPolicy(sess *session.Session, bucket *string) error {
	// snippet-start:[s3.go.set_bucket_policy.call]
	svc := s3.New(sess)

	readOnlyAnonUserPolicy := map[string]interface{}{
		"Version": "2012-10-17",
		"Statement": []map[string]interface{}{
			{
				"Sid":       "AddPerm",
				"Effect":    "Allow",
				"Principal": "*",
				"Action": []string{
					"s3:GetObject",
				},
				"Resource": []string{
					"arn:aws:s3:::" + *bucket + "/*",
				},
			},
		},
	}

	policy, err := json.Marshal(readOnlyAnonUserPolicy)
	if err != nil {
		return err
	}

	_, err = svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
		Bucket: bucket,
		Policy: aws.String(string(policy)),
	})
	// snippet-end:[s3.go.set_bucket_policy.call]
	if err != nil {
		return err
	}

	return nil
}

func main() {
	// snippet-start:[s3.go.set_bucket_policy.args]
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}
	// snippet-end:[s3.go.set_bucket_policy.args]

	// snippet-start:[s3.go.set_bucket_policy.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[s3.go.set_bucket_policy.session]

	err := SetBucketPolicy(sess, bucket)
	if err != nil {
		fmt.Println("Got an error setting bucket policy:")
		fmt.Println(err)
		return
	}

	fmt.Println("Set the bucket policy")
}

// snippet-end:[s3.go.set_bucket_policy]
