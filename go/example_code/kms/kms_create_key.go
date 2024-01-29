// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/kms"

	"fmt"
	"os"
)

// Create an AWS KMS key (KMS key)
// Since we are only encrypting small amounts of data (4 KiB or less) directly,
// a KMS key is fine for our purposes.
// For larger amounts of data,
// use the KMS key to encrypt a data encryption key (DEK).

func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create KMS service client
	svc := kms.New(sess)

	// Create the key
	result, err := svc.CreateKey(&kms.CreateKeyInput{
		Tags: []*kms.Tag{
			{
				TagKey:   aws.String("CreatedBy"),
				TagValue: aws.String("ExampleUser"),
			},
		},
	})

	if err != nil {
		fmt.Println("Got error creating key: ", err)
		os.Exit(1)
	}

	fmt.Println(*result.KeyMetadata.KeyId)
}
