// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"

	"encoding/json"
	"fmt"
	"os"
)

func main() {
	bucket := "myBucket"

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and the region from the shard configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := s3.New(sess)

	PolicyDoc := map[string]interface{}{
		"Version": "2012-10-17",
		"Statement": []map[string]interface{}{
			{
				"Sid":       "DenyIncorrectEncryptionHeader",
				"Effect":    "Deny",
				"Principal": "*",
				"Action":    "s3:PutObject",
				"Resource":  "arn:aws:s3:::" + bucket + "/*",
				"Condition": map[string]interface{}{
					"StringNotEquals": map[string]interface{}{
						"s3:x-amz-server-side-encryption": "aws:kms",
					},
				},
			},
			{
				"Sid":       "DenyUnEncryptedObjectUploads",
				"Effect":    "Deny",
				"Principal": "*",
				"Action":    "s3:PutObject",
				"Resource":  "arn:aws:s3:::" + bucket + "/*",
				"Condition": map[string]interface{}{
					"Null": map[string]interface{}{
						"s3:x-amz-server-side-encryption": "true",
					},
				},
			},
		},
	}

	// Marshal the policy into a JSON value so that it can be sent to S3.
	policy, err := json.Marshal(PolicyDoc)
	if err != nil {
		fmt.Println("Error marshalling policy:")
		fmt.Println(err.Error())
		os.Exit(1)
	}

	input := &s3.PutBucketPolicyInput{
		Bucket: aws.String(bucket),
		Policy: aws.String(string(policy)),
	}

	_, err = svc.PutBucketPolicy(input)
	if err != nil {
		fmt.Println("Got an error adding policy to bucket " + bucket + ":")
		fmt.Println(err.Error())
		os.Exit(1)
	}

	fmt.Println("Set policy for " + bucket)
}
