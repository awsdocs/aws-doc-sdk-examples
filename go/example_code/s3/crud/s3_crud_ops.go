// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"errors"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
)

// Functions to perform CRUD (create, read, update, delete) operations in S3

// CreateBucket creates a bucket
func CreateBucket(sess *session.Session, bucket string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Create the S3 Bucket
	_, err := svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	// Wait until bucket is created before finishing
	err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	return nil
}

// GetBucket determines whether we have this bucket
func GetBucket(sess *session.Session, bucket string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Do we have this Bucket?
	_, err := svc.HeadBucket(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	return nil
}

// HasACL determines whether the bucket has read-only ACL
func HasACL(sess *session.Session, bucket string) error {
	// Create S3 service client
	svc := s3.New(sess)

	acl, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	// Determine whether the group allusers has read permission
	for _, g := range acl.Grants {
		if *g.Grantee.Type == "Group" && *g.Grantee.URI == "http://acs.amazonaws.com/groups/global/AllUsers" && *g.Permission == "READ" {
			return nil
		}
	}

	return errors.New("All users do not have read access")
}

// UpdateBucket changes the bucket to give all users read permission
func UpdateBucket(sess *session.Session, bucket string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Give all users read permission
	_, err := svc.PutBucketAcl(&s3.PutBucketAclInput{
		ACL:    aws.String("public-read"),
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	// Do all users have read permission?
	err = HasACL(sess, bucket)
	if err != nil {
		return err
	}

	return nil
}

// DeleteBucket deletes a bucket
func DeleteBucket(sess *session.Session, bucket string) error {
	// Create S3 service client
	svc := s3.New(sess)

	// Delete the S3 Bucket
	_, err := svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	// Wait until bucket is gone before finishing
	err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	if err != nil {
		return err
	}

	// Make sure it's really gone
	_, err = svc.HeadBucket(&s3.HeadBucketInput{
		Bucket: aws.String(bucket),
	})
	// We expect this to fail if bucket does not exist
	if err != nil {
		return nil
	}

	return errors.New("Could not delete bucket")
}

func main() {}
