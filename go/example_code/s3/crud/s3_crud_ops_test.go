// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"
	"testing"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"

	guuid "github.com/google/uuid"
)

func TestBucketCrudOps(t *testing.T) {
	region := "us-west-2"
	// Create unique bucket name
	id := guuid.New()
	bucketName := id.String()

	fmt.Println("Bucket name:   " + bucketName)
	fmt.Println("")

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials
	// Create a Session with a custom region
	sess := session.Must(session.NewSession(&aws.Config{
		Region: aws.String(region),
	}))

	// Try to create bucket
	err := CreateBucket(sess, bucketName)
	if err != nil {
		t.Errorf("Could not create bucket")
		os.Exit(1)
	}

	fmt.Println("Created bucket", bucketName)

	// Try to access bucket
	err = GetBucket(sess, bucketName)
	if err != nil {
		t.Errorf("Could not get bucket")
	}

	fmt.Println("Read bucket   ", bucketName)

	// Try to update bucket
	err = UpdateBucket(sess, bucketName)
	if err != nil {
		t.Errorf("Could not update bucket")
	}

	fmt.Println("Updated bucket", bucketName)

	// Try to delete bucket
	err = DeleteBucket(sess, bucketName)
	if err != nil {
		t.Errorf("Could not delete bucket")
	} else {
		fmt.Println("Deleted bucket", bucketName)
	}

	fmt.Println("")
}
