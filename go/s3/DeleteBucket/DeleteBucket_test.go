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
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/google/uuid"
)

type Config struct {
    Bucket string `json:"Bucket"`
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

func getBuckets(sess *session.Session) (*s3.ListBucketsOutput, error) {
    svc := s3.New(sess)

    result, err := svc.ListBuckets(&s3.ListBucketsInput{})
    if err != nil {
        return nil, err
    }

    return result, nil
}

func TestCreateBucket(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // If we don't have a name in config.json
    // create a bucket with a random name
    if globalConfig.Bucket == "" {
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()

        err = createBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created bucket " + globalConfig.Bucket)
    }

    // Get original list of buckets before removing one
    result, err := getBuckets(sess)
    if err != nil {
        t.Fatal(err)
    }

    numBuckets := len(result.Buckets)

    err = RemoveBucket(sess, &globalConfig.Bucket)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted bucket " + globalConfig.Bucket)

    result, err = getBuckets(sess)
    if err != nil {
        t.Fatal(err)
    }

    if len(result.Buckets) != numBuckets-1 {
        t.Log("Not sure " + globalConfig.Bucket + " got deleted")
    }

    for _, b := range result.Buckets {
        if *b.Name == globalConfig.Bucket {
            t.Fatal("DID NOT DELETE" + globalConfig.Bucket)
        }
    }
}
