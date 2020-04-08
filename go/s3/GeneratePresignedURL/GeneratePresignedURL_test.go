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
    Bucket string `json:"Bucket"`
    Key    string `json:"Key"`
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

    t.Log("Bucket:  " + globalConfig.Bucket)
    t.Log("Key:     " + globalConfig.Key)

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

func uploadFile(sess *session.Session, bucket, key *string) error {
    file, err := os.Open(*key)
    if err != nil {
        return err
    }

    defer file.Close()

    uploader := s3manager.NewUploader(sess)
    _, err = uploader.Upload(&s3manager.UploadInput{
        Bucket: bucket,
        Key:    key,
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

func TestGeneratePresignedURL(t *testing.T) {
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

    bucketCreated := false

    if globalConfig.Bucket == "" || globalConfig.Key == "" {
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()
        globalConfig.Key = "test.txt"

        err := createBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created " + globalConfig.Bucket)

        err = uploadFile(sess, &globalConfig.Bucket, &globalConfig.Key)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Created " + globalConfig.Bucket)
        bucketCreated = true
    }

    urlStr, err := GetPresignedURL(sess, &globalConfig.Bucket, &globalConfig.Key)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("The pre-signed URL: " + urlStr + " is valid for 15 minutes")

    if bucketCreated {
        err := deleteBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted " + globalConfig.Bucket)
    }
}
