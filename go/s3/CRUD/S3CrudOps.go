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

// Functions to perform CRUD (create, read, update, delete) operations in Amazon S3

// MakeBucket creates an S3 bucket
func MakeBucket(svc s3iface.S3API, bucket *string) error {
    // snippet-start: [s3.go.crud_ops.call]
    // Create the S3 bucket
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

func GetBucket(svc s3iface.S3API, bucket *string) error {
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

// HasACL returns nil if the S3 bucket has a read-only ACL
func HasACL(svc s3iface.S3API, bucket *string) error {
    // snippet-start: [s3.go.crud_ops.get_acl]
    acl, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.get_acl]
    if err != nil {
        return err
    }

    // Determine whether the group AllUsers has read permission
    // snippet-start: [s3.go.crud_ops.check_grants]
    for _, g := range acl.Grants {
        if *g.Grantee.Type == "Group" && *g.Grantee.URI == "http://acs.amazonaws.com/groups/global/AllUsers" && *g.Permission == "READ" {
            // snippet-end: [s3.go.crud_ops.check_grants]
            return nil
        }
    }

    return errors.New("All users do not have read access")
}

// UpdateBucket changes the S3 bucket to give all users read permission
func UpdateBucket(svc s3iface.S3API, bucket *string) error {
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

// RemoveBucket deletes an S3 bucket
func RemoveBucket(svc s3iface.S3API, bucket *string) error {
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

func waitForDelete(svc s3iface.S3API, bucket *string) error {
    // snippet-start: [s3.go.crud_ops.wait_delete]
    err := svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    // snippet-end: [s3.go.crud_ops.wait_delete]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start: [s3.go.crud_ops.args]
    bucket := flag.String("b", "", "The name of the S3 bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply an S3 bucket name (-b BUCKET)")
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
        fmt.Println("Got error creating S3 bucket:")
        fmt.Println(err)
        return
    }

    err = waitForCreate(svc, bucket)
    if err != nil {
        fmt.Println("Got error creating S3 bucket:")
        fmt.Println(err)
        return
    }

    err = GetBucket(svc, bucket)
    if err != nil {
        fmt.Println("Got error reading S3 bucket:")
        fmt.Println(err)
        return
    }

    err = UpdateBucket(svc, bucket)
    if err != nil {
        fmt.Println("Got error updating S3 bucket:")
        fmt.Println(err)
        return
    }

    err = RemoveBucket(svc, bucket)
    if err != nil {
        fmt.Println("Got error deleting S3 bucket:")
        fmt.Println(err)
        return
    }

    err = waitForDelete(svc, bucket)
    if err != nil {
        fmt.Println("Got error deleting S3 bucket:")
        fmt.Println(err)
        return
    }

    fmt.Println("Successfully ran CRUD operations on S3 bucket " + *bucket)
}
// snippet-end: [s3.go.crud_ops]
