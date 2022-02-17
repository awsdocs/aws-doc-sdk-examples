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
    "strings"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"

    "github.com/google/uuid"
)

type Config struct {
    Bucket    string `json:"Bucket"`
    IndexPage string `json:"IndexPage"`
    ErrorPage string `json:"ErrorPage"`
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

    t.Log("Bucket:    " + globalConfig.Bucket)
    t.Log("IndexPage: " + globalConfig.IndexPage)
    t.Log("ErrorPage: " + globalConfig.ErrorPage)

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

func createObject(sess *session.Session, bucket *string, key *string, content *string) error {
    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader(*content),
        Bucket: bucket,
        Key:    key,
    })
    if err != nil {
        return err
    }

    return nil
}

func clearBucket(sess *session.Session, bucket *string) error {
    svc := s3.New(sess)
    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: bucket,
    })

    err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
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

func TestSetBucketWebsite(t *testing.T) {
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

    if globalConfig.Bucket == "" {
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()

        err := createBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        if globalConfig.IndexPage == "" {
            globalConfig.IndexPage = "Index.html"
            content := "<html><body><p>This is the index</p></body></html>"
            err := createObject(sess, &globalConfig.Bucket, &globalConfig.IndexPage, &content)
            if err != nil {
                t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
                t.Fatal(err)
            }
        }

        if globalConfig.ErrorPage == "" {
            globalConfig.ErrorPage = "Error.html"
            content := "<html><body><p>ERROR!</p></body></html>"
            err := createObject(sess, &globalConfig.Bucket, &globalConfig.ErrorPage, &content)
            if err != nil {
                t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
                t.Fatal(err)
            }
        }

        bucketCreated = true
        t.Log("Created bucket " + globalConfig.Bucket)
    }

    err = SetWebPage(sess, &globalConfig.Bucket, &globalConfig.IndexPage, &globalConfig.ErrorPage)
    if err != nil {
        t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
        t.Fatal(err)
    }

    if bucketCreated {
        err := clearBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        err = deleteBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted " + globalConfig.Bucket)
    }

}
