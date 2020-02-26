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
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)

type config struct {
    Bucket     string `json:"Bucket"`
    Object     string `json:"Object"`
    UseTimeout bool   `json:"UseTimeout"`
    ShowObject bool   `json:"ShowObject"`
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

func TestCustomHTTPClient(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal("Could not get configuration values")
    }

    if globalConfig.Bucket == "" || globalConfig.Object == "" {
        msg := "You must supply the name of the bucket and object in " + configFileName
        t.Fatal(msg)
    }

    t.Log("Getting object " + globalConfig.Object + " from bucket " + globalConfig.Bucket)

    var body io.ReadCloser

    if globalConfig.UseTimeout {
        // Initialize a session that the SDK uses to load
        // credentials from the shared credentials file (~/.aws/credentials)
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

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

        client := s3.New(sess)

        obj, err := client.GetObject(&s3.GetObjectInput{
            Bucket: &globalConfig.Bucket,
            Key:    &globalConfig.Object,
        })
        if err != nil {
            t.Fatal(err)
        }

        body = obj.Body
    }

    showObject(t, globalConfig.ShowObject, body)
}

