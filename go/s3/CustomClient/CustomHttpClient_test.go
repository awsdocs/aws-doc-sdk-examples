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
    "bytes"
    "encoding/json"
    "io"
    "io/ioutil"
    "strings"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/google/uuid"
)

type config struct {
    Bucket        string `json:"Bucket"`
    Object        string `json:"Object"`
    UseTimeout    bool   `json:"UseTimeout"`
    ShowObject    bool   `json:"ShowObject"`
    BucketCreated bool
    ObjectCreated bool
}

var configFileName = "config.json"

var globalConfig config

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

    t.Log("Bucket name:  " + globalConfig.Bucket)
    t.Log("Object name:  " + globalConfig.Object)

    if globalConfig.UseTimeout {
        t.Log("Use timeout?: enabled")
    } else {
        t.Log("Use timeout?: disabled")
    }

    if globalConfig.ShowObject {
        t.Log("Show object?: enabled")
    } else {
        t.Log("Show object?: disabled")
    }

    globalConfig.BucketCreated = false

    return nil
}

func createBucket(sess *session.Session, id uuid.UUID) error {
    globalConfig.Bucket = "custom-bucket-" + id.String()

    svc := s3.New(sess)

    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: aws.String(globalConfig.Bucket),
    })
    if err != nil {
        return err
    }

    globalConfig.BucketCreated = true

    return nil
}

func createObject(sess *session.Session, bucket *string, id uuid.UUID) error {
    globalConfig.Object = "custom-object-" + id.String()

    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: bucket,
        Key:    &globalConfig.Object,
    })
    if err != nil {
        return err
    }

    globalConfig.ObjectCreated = true

    return nil
}

func showObject(t *testing.T, show bool, body io.ReadCloser) {
    if show {
        // Convert body from IO.ReadCloser to string:
        buf := new(bytes.Buffer)

        _, err := buf.ReadFrom(body)
        if err != nil {
            t.Fatal(err)
        }

        newBytes := buf.String()
        s := string(newBytes)

        t.Log("Bucket item as string:")
        t.Log(s)
    }
}

func deleteObject(sess *session.Session, bucket *string, object *string) error {
    svc := s3.New(sess)

    _, err := svc.DeleteObject(&s3.DeleteObjectInput{
        Bucket: bucket,
        Key:    object,
    })
    if err != nil {
        return err
    }

    return svc.WaitUntilObjectNotExists(&s3.HeadObjectInput{
        Bucket: bucket,
        Key:    object,
    })
}

func deleteBucket(sess *session.Session, bucket *string) error {
    svc := s3.New(sess)

    _, err := svc.DeleteBucket(&s3.DeleteBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    return svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
}

func TestCustomHTTPClient(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal("Could not get configuration values")
    }

    var body io.ReadCloser
    id := uuid.New()

    if globalConfig.UseTimeout {
        // Initialize a session that the SDK uses to load
        // credentials from the shared credentials file (~/.aws/credentials)
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        if globalConfig.Bucket == "" {
            err := createBucket(sess, id)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Created bucket " + globalConfig.Bucket)
        }

        if globalConfig.Object == "" {
            err := createObject(sess, &globalConfig.Bucket, id)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Created object " + globalConfig.Object)
        }

        // Get object using 20 second timeout
        body, err = GetObjectWithTimeout(sess, &globalConfig.Bucket, &globalConfig.Object)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Got " + globalConfig.Object + " from " + globalConfig.Bucket)
    } else {
        // Creating a SDK session using the custom HTTP client
        // and use that session to create S3 client.
        httpClient, err := NewHTTPClientWithSettings(HTTPClientSettings{
            Connect:          5 * time.Second,
            ExpectContinue:   1 * time.Second,
            IdleConn:         90 * time.Second,
            ConnKeepAlive:    30 * time.Second,
            MaxAllIdleConns:  100,
            MaxHostIdleConns: 10,
            ResponseHeader:   5 * time.Second,
            TLSHandshake:     5 * time.Second,
        })
        if err != nil {
            t.Fatal(err)
        }

        sess := session.Must(session.NewSession(&aws.Config{
            HTTPClient: httpClient,
        }))

        if globalConfig.Bucket == "" {
            err := createBucket(sess, id)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Created bucket " + globalConfig.Bucket)
        }

        if globalConfig.Object == "" {
            err := createObject(sess, &globalConfig.Bucket, id)
            if err != nil {
                t.Fatal(err)
            }

            t.Log("Created object " + globalConfig.Object)
        }

        svc := s3.New(sess)

        obj, err := svc.GetObject(&s3.GetObjectInput{
            Bucket: &globalConfig.Bucket,
            Key:    &globalConfig.Object,
        })
        if err != nil {
            t.Fatal(err)
        }

        body = obj.Body
    }

    showObject(t, globalConfig.ShowObject, body)

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    if globalConfig.ObjectCreated {
        // deleteObject(sess *session.Session, bucket *string, object *string)
        err := deleteObject(sess, &globalConfig.Bucket, &globalConfig.Object)
        if err != nil {
            t.Log("You'll have to delete object " + globalConfig.Object + " yourself")
            t.Log("You'll have to delete bucket " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted object " + globalConfig.Object + " from bucket " + globalConfig.Bucket)
    }

    if globalConfig.BucketCreated {
        err := deleteBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Log("You'll have to delete bucket " + globalConfig.Bucket + " yourself")
            t.Fatal(err)
        }

        t.Log("Deleted bucket " + globalConfig.Bucket)
    }
}
