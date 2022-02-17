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
    "strconv"
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

    t.Log("Bucket: " + globalConfig.Bucket)

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

func addFile(sess *session.Session, bucket *string, filename string) error {
    file, err := os.Open(filename)
    if err != nil {
        fmt.Println("Unable to open file " + filename)
        return err
    }

    defer file.Close()

    uploader := s3manager.NewUploader(sess)

    _, err = uploader.Upload(&s3manager.UploadInput{
        Bucket: bucket,
        Key:    aws.String(filename),
        Body:   file,
    })
    if err != nil {
        return err
    }

    return nil
}

func getObjects(sess *session.Session, bucket *string) (*s3.ListObjectsV2Output, error) {
    svc := s3.New(sess)

    // Get the list of items
    resp, err := svc.ListObjectsV2(&s3.ListObjectsV2Input{Bucket: bucket})
    if err != nil {
        return nil, err
    }

    return resp, nil
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
func TestDeleteObject(t *testing.T) {
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
            t.Log("Got error creating bucket:")
            t.Fatal(err)
        }

        bucketCreated = true
        t.Log("Created bucket " + globalConfig.Bucket)

        resp, err := getObjects(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("Got error retrieving list of objects:")
            t.Fatal(err)
        }

        t.Log("Found " + strconv.Itoa(len(resp.Contents)) + " items in " + globalConfig.Bucket + " (should be 0)")

        err = addFile(sess, &globalConfig.Bucket, "test1.txt")
        if err != nil {
            t.Log("Got error adding test1.txt")
            t.Fatal(err)
        }

        err = addFile(sess, &globalConfig.Bucket, "test2.txt")
        if err != nil {
            t.Log("Got error adding test2.txt")
            t.Fatal(err)
        }

        resp, err = getObjects(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("Got error retrieving list of objects:")
            t.Fatal(err)
        }

        t.Log("Found " + strconv.Itoa(len(resp.Contents)) + " items in " + globalConfig.Bucket + " (should be 2)")
    }

    err = DeleteItems(sess, &globalConfig.Bucket)
    if err != nil {
        t.Log("Could not empty " + globalConfig.Bucket)
        t.Fatal(err)
    }

    t.Log("Emptied " + globalConfig.Bucket)

    resp, err := getObjects(sess, &globalConfig.Bucket)
    if err != nil {
        t.Log("Got error retrieving list of objects:")
        t.Fatal(err)
    }

    t.Log("Found " + strconv.Itoa(len(resp.Contents)) + " items in " + globalConfig.Bucket + " (should be 0)")

    if bucketCreated {
        err := deleteBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete bucket " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted bucket " + globalConfig.Bucket)
    }

}
