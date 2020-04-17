// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

// snippet-start: [s3.go.crud_ops]
package main

// snippet-start: [s3.go.crud_ops.imports]
import (
    "errors"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3iface"
)
// snippet-end: [s3.go.crud_ops.imports]

// Functions to perform CRUD (create, read, update, delete) operations in S3

// MakeBucket creates a bucket
func MakeBucket(svc s3iface.S3API, bucket *string) error {
    // snippet-start: [s3.go.crud_ops.call]
    // Create the S3 Bucket
    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.call]
    if err != nil {
        return err
    }

    return nil
}

func waitForCreate(svc s3iface.S3API, bucket *string) error {
    // Wait until bucket is created before finishing
    // snippet-start: [s3.go.crud_ops.wait_create]
    err := svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.wait_create]
    if err != nil {
        return err
    }

    return nil
}

// GetBucket returns nil if we own this bucket
func GetBucket(svc s3iface.S3API, bucket *string) error {
    // Do we have this Bucket?
    // snippet-start: [s3.go.crud_ops.head_bucket]
    _, err := svc.HeadBucket(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.head_bucket]
    if err != nil {
        return err
    }

    return nil
}

// HasACL returns nil if the bucket has a read-only ACL
func HasACL(svc s3iface.S3API, bucket *string) error {
    // snippet-start: [s3.go.crud_ops.get_acl]
    acl, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.get_acl]
    if err != nil {
        return err
    }

    // Determine whether the group allusers has read permission
    // snippet-start: [s3.go.crud_ops.check_grants]
    for _, g := range acl.Grants {
        if *g.Grantee.Type == "Group" && *g.Grantee.URI == "http://acs.amazonaws.com/groups/global/AllUsers" && *g.Permission == "READ" {
            // snippet-end: [s3.go.crud_ops.check_grants]
            return nil
        }
    }

    return errors.New("All users do not have read access")
}

// UpdateBucket changes the bucket to give all users read permission
func UpdateBucket(svc s3iface.S3API, bucket *string) error {
    // Give all users read permission
    // snippet-start: [s3.go.crud_ops.put_acl]
    _, err := svc.PutBucketAcl(&s3.PutBucketAclInput{
        ACL:    aws.String("public-read"),
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.put_acl]
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
    // snippet-start: [s3.go.crud_ops.delete_bucket]
    _, err := svc.DeleteBucket(&s3.DeleteBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.delete_bucket]
    if err != nil {
        return err
    }

    return nil
}

func waitForBucketGone(svc s3iface.S3API, bucket *string) error {
    // Wait until bucket is gone before finishing
    // snippet-start: [s3.go.crud_ops.wait_deleted]
    err := svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.wait_deleted]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start: [s3.go.crud_ops.args]
    bucket := flag.String("b", "", "The name of the bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET)")
        return
    }
    // snippet-end: [s3.go.crud_ops.args]

    // snippet-start: [s3.go.crud_ops.session_service]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := s3.New(sess)
    // snippet-end: [s3.go.crud_ops.session_service]

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
// snippet-end: [s3.go.crud_ops]
