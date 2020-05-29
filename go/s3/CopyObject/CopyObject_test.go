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
    "encoding/json"
    "fmt"
    "io/ioutil"
    "os"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"

    "github.com/google/uuid"
)

type Config struct {
    SourceBucket string `json:"SourceBucket"`
    TargetBucket string `json:"TargetBucket"`
    Item         string `json:"Item"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    t.Log("SourceBucket: " + globalConfig.SourceBucket)
    t.Log("TargetBucket: " + globalConfig.TargetBucket)
    t.Log("Item:         " + globalConfig.Item)

    return nil
}

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

func putFile(sess *session.Session, bucket *string, filename *string) error {
    file, err := os.Open(*filename)
    if err != nil {
        fmt.Println("Unable to open file " + *filename)
        return err
    }

    defer file.Close()

    uploader := s3manager.NewUploader(sess)

    _, err = uploader.Upload(&s3manager.UploadInput{
        Bucket: bucket,
        Key:    filename,
        Body:   file,
    })
    if err != nil {
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

func TestCopyObject(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    createdBuckets := false

    if globalConfig.SourceBucket == "" || globalConfig.TargetBucket == "" {
        id := uuid.New()
        globalConfig.SourceBucket = "source-bucket-" + id.String()
        globalConfig.TargetBucket = "target-bucket-" + id.String()

        err := createBucket(sess, &globalConfig.SourceBucket)
        if err != nil {
            t.Log("Could not create bucket " + globalConfig.SourceBucket)
            t.Fatal(err)
        }

        t.Log("Created bucket " + globalConfig.SourceBucket)

        err = createBucket(sess, &globalConfig.TargetBucket)
        if err != nil {
            t.Log("Could not create bucket " + globalConfig.TargetBucket)
            t.Fatal(err)
        }

        t.Log("Created bucket " + globalConfig.TargetBucket)

        if globalConfig.Item == "" {
            globalConfig.Item = "test.txt"
        }

        err = putFile(sess, &globalConfig.SourceBucket, &globalConfig.Item)
        if err != nil {
            t.Log("Could not upload " + globalConfig.Item + " to " + globalConfig.SourceBucket)
            t.Fatal(err)
        }

        t.Log("Uploaded " + globalConfig.Item + " to " + globalConfig.SourceBucket)

        createdBuckets = true
    }

    err = CopyItem(sess, &globalConfig.SourceBucket, &globalConfig.TargetBucket, &globalConfig.Item)
    if err != nil {
        t.Log("Could not copy " + globalConfig.Item + " to " + globalConfig.TargetBucket)
        t.Fatal(err)
    }

    t.Log("Copied " + globalConfig.Item + " to " + globalConfig.TargetBucket)

    if createdBuckets {
        err := deleteBucket(sess, &globalConfig.SourceBucket)
        if err != nil {
            t.Log("You must delete buckets " + globalConfig.SourceBucket + " and " + globalConfig.TargetBucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted bucket " + globalConfig.SourceBucket)

        err = deleteBucket(sess, &globalConfig.TargetBucket)
        if err != nil {
            t.Log("You must delete bucket " + globalConfig.TargetBucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted bucket " + globalConfig.TargetBucket)
    }
}
