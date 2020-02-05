/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "strconv"
    "strings"
    "testing"
    "time"

    guuid "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)

func createBucket(sess *session.Session, bucketName string) error {
    // Create S3 service client
    svc := s3.New(sess)

    // Create the S3 Bucket
    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: aws.String(bucketName),
    })
    if err != nil {
        return err
    }

    // Wait until bucket is created before finishing
    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: aws.String(bucketName),
    })
    if err != nil {
        return err
    }

    return nil
}

func populateBucket(sess *session.Session, bucket string) error {
    s3Svc := s3.New(sess)

    // Create an uploader with S3 client and default options
    uploader := s3manager.NewUploaderWithClient(s3Svc)

    // Add some dummy data to the bucket
    text := "abc"
    body := strings.NewReader(text)

    upParams := &s3manager.UploadInput{
        Bucket: &bucket,
        Key:    aws.String("dummy"),
        Body:   body,
    }

    _, err := uploader.Upload(upParams)

    return err
}

// listBuckets lists the S3 buckets with the given prefix
func listBuckets(sess *session.Session, prefix string) error {
    // Create S3 service client
    svc := s3.New(sess)

    result, err := svc.ListBuckets(nil)
    if err != nil {
        fmt.Println("Could not list buckets")
        return err
    }

    for _, b := range result.Buckets {
        if strings.HasPrefix(*b.Name, prefix) {
            fmt.Println(*b.Name)
        }
    }

    return nil
}

func TestAudioToText(t *testing.T) {
    // When the test started:
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    fmt.Println("Starting unit test at " + nowString)
    fmt.Println("")

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create a unique GUID
    id := guuid.New()
    defaultName := id.String()

    // Create 3 dummy buckets with prefix "dummy-"
    prefix := "dummy-"

    for i := 0; i < 3; i++ {
        name := prefix + defaultName + strconv.Itoa(i)
        err := createBucket(sess, name)
        if err != nil {
            fmt.Println("Could not create bucket " + name)
            os.Exit(1)
        } else {
            fmt.Println("Created bucket " + name)

            err = populateBucket(sess, name)
        }
    }

    // List the buckets
    fmt.Println("")
    fmt.Println("Here are your buckets that start with " + prefix)
    listBuckets(sess, prefix)

    // Now delete them
    err := deleteBucketsByPrefix(sess, prefix)
    if err != nil {
        fmt.Println("Could not delete buckets with prefix " + prefix)
    } else {
        fmt.Println("Deleted buckets with prefix " + prefix)
    }

    // List the buckets
    fmt.Println("")
    fmt.Println("Now here are your buckets that start with " + prefix)
    listBuckets(sess, prefix)
}
