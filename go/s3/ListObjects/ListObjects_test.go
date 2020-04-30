/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "strings"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
    "github.com/google/uuid"
)

func createBucket(sess *session.Session, bucket *string) error {
    svc := s3.New(sess)

    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    return nil
}

func createObject(sess *session.Session, bucket *string, key *string) error {
    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: bucket,
        Key:    key,
    })
    if err != nil {
        fmt.Println("Got error putting object in bucket:")
        return err
    }

    return nil
}

func deleteBucket(sess *session.Session, bucket *string) error {
    svc := s3.New(sess)

    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: bucket,
    })

    err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
    if err != nil {
        return err
    }

    _, err = svc.DeleteBucket(&s3.DeleteBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    return nil
}

func TestListBuckets(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create a new bucket and add key to it
    id := uuid.New()
    bucket := "test-bucket-" + id.String()

    err := createBucket(sess, &bucket)
    if err != nil {
        t.Fatal(err)
    }

    // Get original list of objects
    result, err := GetObjects(sess, &bucket)
    if err != nil {
        t.Fatal(err)
    }

    numObjects := *result.KeyCount + int64(1)

    key := "TestFile.txt"

    err = createObject(sess, &bucket, &key)
    if err != nil {
        t.Fatal(err)
    }

    // Get new list of objects
    result, err = GetObjects(sess, &bucket)
    if err != nil {
        t.Fatal(err)
    }

    lenEqual := *result.KeyCount == numObjects

    if !lenEqual {
        t.Log("Did not get new key in new list")
    }

    foundKey := false

    for _, obj := range result.Contents {
        if *obj.Key == key {
            foundKey = true
            break
        }
    }

    if foundKey {
        t.Log("Found key in bucket")
    } else {
        t.Log("Did not find key in bucket")
    }

    err = deleteBucket(sess, &bucket)
    if err != nil {
        t.Log("You'll have to delete " + bucket + " yourself")
    }
}
