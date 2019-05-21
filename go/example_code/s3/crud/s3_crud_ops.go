// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[s3_crud_ops.go performs CRUD (create, read, update, delete) operations on an S3 bucket.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[CreateBucket function]
// snippet-keyword:[ListBuckets function]
// snippet-keyword:[GetBucketAcl function]
// snippet-keyword:[PutBucketAcl function]
// snippet-keyword:[DeleteBucket function]
// snippet-keyword:[WaitUntilBucketExists function]
// snippet-keyword:[WaitUntilBucketNotExists function]
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
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Functions to perform CRUD (create, read, update, delete) operations in S3

// CreateBucket creates a bucket
func CreateBucket(bucket string) (bool, error) {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Create the S3 Bucket
	_, err = svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return false, err
	}

	// Wait until bucket is created before finishing
	err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return false, err
	}

	return true, nil
}

// ReadBucket determines whether we have this bucket
func ReadBucket(bucket string) (bool, error) {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Do we have this Bucket?
	result, err := svc.ListBuckets(nil)
	if err != nil {
		return false, err
	}

	for _, b := range result.Buckets {
		if *b.Name == bucket {
			return true, nil
		}
	}

	return false, nil
}

// HasACL determines whether the bucket has read-only ACL
func HasACL(bucket string) (bool, error) {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	acl, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return false, err
	}

	// Determine whether the group allusers has read permission
	for _, g := range acl.Grants {
		if *g.Grantee.Type == "Group" && *g.Grantee.URI == "http://acs.amazonaws.com/groups/global/AllUsers" && *g.Permission == "READ" {
			return true, nil
		}
	}

	return false, nil
}

// UpdateBucket changes the bucket to give all users read permission
func UpdateBucket(bucket string) (bool, error) {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Give all users read permission
	svc.PutBucketAcl(&s3.PutBucketAclInput{
		ACL:    aws.String("public-read"),
		Bucket: aws.String(bucket),
	})

	// Find the bucket
	result, err := svc.ListBuckets(nil)
	if err != nil {
		return false, err
	}

	for _, b := range result.Buckets {
		if *b.Name == bucket {
			// Do all users have read permission?
			hasACL, err := HasACL(bucket)
			if err != nil {
				return false, err
			}

			return hasACL, nil
		}
	}

	return false, nil
}

// DeleteBucket deletes a bucket
func DeleteBucket(bucket string) (bool, error) {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create S3 service client
	svc := s3.New(sess)

	// Delete the S3 Bucket
	_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return false, err
	}

	// Wait until bucket is gone before finishing
	err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return false, err
	}

	// Make sure it's really gone
	result, err := svc.ListBuckets(nil)
	if err != nil {
		return false, err
	}

	for _, b := range result.Buckets {
		if *b.Name == bucket {
			return false, nil
		}
	}

	return true, nil
}

func main() {}
