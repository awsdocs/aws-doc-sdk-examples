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
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
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

func deleteBucket(sess *session.Session, bucket *string) error {
    svc := s3.New(sess)

    _, err := svc.DeleteBucket(&s3.DeleteBucketInput{
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

    result, err := GetAllBuckets(sess)
    if err != nil {
        t.Fatal(err)
    }

    numBuckets := len(result.Buckets)

    // Create a bucket, make sure it's in the list,
    // make sure the list is one item larger, then delete the bucket
    id := uuid.New()
    bucketName := "some-prefix-" + id.String()

    err = createBucket(sess, &bucketName)
    if err != nil {
        t.Fatal(err)
    }

    result, err = GetAllBuckets(sess)
    if err != nil {
        t.Fatal(err)
    }

    lenEqual := len(result.Buckets) == numBuckets+1

    if !lenEqual {
        t.Log("Adding a bucket didn't work")
    }

    foundBucket := false

    for _, b := range result.Buckets {
        if *b.Name == bucketName {
            foundBucket = true
            break
        }
    }

    if foundBucket {
        t.Log("Found bucket in list")
    } else {
        t.Log("Did not find bucket in list")
    }

    err = deleteBucket(sess, &bucketName)
    if err != nil {
        t.Log("You'll have to delete " + bucketName + " yourself")
    }
}
