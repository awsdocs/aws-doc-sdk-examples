// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"

	"fmt"
	"os"
	"strings"
)

func main() {
	if len(os.Args) != 2 {
		fmt.Println("You must supply a key")
		os.Exit(1)
	}

	key := os.Args[1]
	bucket := "myBucket"
	object := "myItem"

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := s3.New(sess)

	input := &s3.PutObjectInput{
		Body:                 strings.NewReader(object),
		Bucket:               aws.String(bucket),
		Key:                  aws.String(object),
		ServerSideEncryption: aws.String("aws:kms"),
		SSEKMSKeyId:          aws.String(key),
	}

	_, err := svc.PutObject(input)
	if err != nil {
		fmt.Println("Got an error adding object to bucket")
		fmt.Println(err.Error())
		os.Exit(1)
	}

	fmt.Println("Added object " + object + " to bucket " + bucket + " with AWS KMS encryption")
}
