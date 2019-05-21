// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[s3_crud_ops_test.go tests functions in s3_crud_ops.go.]
// snippet-keyword:[Go]
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
	"testing"

	guuid "github.com/google/uuid"
)

func TestBucketCrudOps(t *testing.T) {
	// Create unique bucket name
	id := guuid.New()
	bucketName := id.String()

	fmt.Println("Bucket name:   " + bucketName)
	fmt.Println("")

	// Try to create bucket
	bucketCreated, err := CreateBucket(bucketName)
	if err != nil {
		t.Errorf("Got problems creating bucket: " + err.Error())
	}
	if !bucketCreated {
		t.Errorf("Could not create bucket")
	}

	fmt.Println("Created bucket", bucketName)

	// Try to read bucket
	bucketRead, err := ReadBucket(bucketName)
	if err != nil {
		t.Errorf("Got problems reading bucket: " + err.Error())
	}
	if !bucketRead {
		t.Errorf("Could not read bucket")
	}

	fmt.Println("Read bucket   ", bucketName)

	// Try to update bucket
	bucketUpdated, err := UpdateBucket(bucketName)
	if err != nil {
		t.Errorf("Got problems updating bucket: " + err.Error())
	}
	if !bucketUpdated {
		t.Errorf("Could not update bucket")
	}

	fmt.Println("Updated bucket", bucketName)

	// Try to delete bucket
	bucketDeleted, err := DeleteBucket(bucketName)
	if err != nil {
		t.Errorf("Got problems deleting bucket: " + err.Error())
	}
	if !bucketDeleted {
		t.Errorf("Could not delete bucket")
	}

	fmt.Println("Deleted bucket", bucketName)
	fmt.Println("")
}
