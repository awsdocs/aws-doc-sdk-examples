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
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "github.com/google/uuid"
)

type Config struct {
    Bucket     string `json:"Bucket"`
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

    t.Log("Bucket:     " + globalConfig.Bucket)

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

func TestPutBucketAcl(t *testing.T) {
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
        // Create the resources
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()

        err := createBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        bucketCreated = true
        t.Log("Created bucket " + globalConfig.Bucket)
    }

    result, err := GetBucketACL(sess, &globalConfig.Bucket)
    if err != nil {
        t.Fatal(err)
    }

    fmt.Println("Owner:", *result.Owner.DisplayName)
    fmt.Println("")
    fmt.Println("Grants")

    for _, g := range result.Grants {
        // If we add a canned ACL, the name is nil
        if g.Grantee.DisplayName == nil {
            t.Log("  Grantee:    EVERYONE")
        } else {
            t.Log("  Grantee:   ", *g.Grantee.DisplayName)
        }

        t.Log("  Type:      ", *g.Grantee.Type)
        t.Log("  Permission:", *g.Permission)
    }

    if bucketCreated {
        err := deleteBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete bucket " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted bucket " + globalConfig.Bucket)
    }
}
