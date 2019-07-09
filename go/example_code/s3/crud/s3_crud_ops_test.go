// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[s3_crud_ops_test.go tests functions in s3_crud_ops.go.]
// snippet-keyword:[Go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-05-21]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

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
