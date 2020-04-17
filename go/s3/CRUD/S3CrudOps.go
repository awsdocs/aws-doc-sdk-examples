// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
	"errors"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3iface"
)

// Functions to perform CRUD (create, read, update, delete) operations in S3

// MakeBucket creates a bucket
func MakeBucket(svc s3iface.S3API, bucket *string) error {
	// Create the S3 Bucket
	_, err := svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

func waitForCreate(svc s3iface.S3API, bucket *string) error {
	// Wait until bucket is created before finishing
	err := svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

// GetBucket returns nil if we own this bucket
func GetBucket(svc s3iface.S3API, bucket *string) error {
	// Do we have this Bucket?
	_, err := svc.HeadBucket(&s3.HeadBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

// HasACL returns nil if the bucket has a read-only ACL
func HasACL(svc s3iface.S3API, bucket *string) error {
	acl, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
		Bucket: bucket,
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
func UpdateBucket(svc s3iface.S3API, bucket *string) error {
	// Give all users read permission
	_, err := svc.PutBucketAcl(&s3.PutBucketAclInput{
		ACL:    aws.String("public-read"),
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	// Do all users have read permission?
	err = HasACL(svc, bucket)
	if err != nil {
		return err
	}

	return nil
}

// RemoveBucket deletes a bucket
func RemoveBucket(svc s3iface.S3API, bucket *string) error {
	// Delete the S3 Bucket
	_, err := svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

func waitForBucketGone(svc s3iface.S3API, bucket *string) error {
	// Wait until bucket is gone before finishing
	err := svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

func main() {
	bucket := flag.String("b", "", "The name of the bucket")
	flag.Parse()

	if *bucket == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET)")
		return
	}

	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	svc := s3.New(sess)

	err := MakeBucket(svc, bucket)
	if err != nil {
		fmt.Println("Got error creating bucket:")
		fmt.Println(err)
		return
	}

	err = waitForCreate(svc, bucket)
	if err != nil {
		fmt.Println("Got error creating bucket:")
		fmt.Println(err)
		return
	}

	err = GetBucket(svc, bucket)
	if err != nil {
		fmt.Println("Got error reading bucket:")
		fmt.Println(err)
		return
	}

	err = UpdateBucket(svc, bucket)
	if err != nil {
		fmt.Println("Got error updating bucket:")
		fmt.Println(err)
		return
	}

	err = RemoveBucket(svc, bucket)
	if err != nil {
		fmt.Println("Got error deleting bucket:")
		fmt.Println(err)
		return
	}

	err = waitForBucketGone(svc, bucket)
	if err != nil {
		fmt.Println("Got error deleting bucket:")
		fmt.Println(err)
		return
	}

	fmt.Println("Successfully ran CRUD operations on bucket " + *bucket)
}
