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
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3iface"

    "github.com/google/uuid"
)

// Define a mock struct to be used in your unit tests of myFunc.
type mockS3Client struct {
    s3iface.S3API
}

func (m *mockS3Client) RestoreObject(*s3.RestoreObjectInput) (*s3.RestoreObjectOutput, error) {
    // requestCharged := ""
    // restoreOutputPath := ""
    resp := s3.RestoreObjectOutput{
        RequestCharged:    nil, // was: &requestCharged,
        RestoreOutputPath: nil, // was: &restoreOutputPath,
    }

    return &resp, nil
}

type Config struct {
    Bucket string `json:"Bucket"`
    Item   string `json:"Item"`
    Days   int64  `json:"Days"`
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

    if globalConfig.Days < 1 {
        globalConfig.Days = 1
    }

    if globalConfig.Days > 365 {
        globalConfig.Days = 365
    }

    t.Log("Bucket: " + globalConfig.Bucket)
    t.Log("Item:   " + globalConfig.Item)
    t.Log("Days:   " + strconv.Itoa(int(globalConfig.Days)))

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

    if globalConfig.Bucket == "" || globalConfig.Item == "" {
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()
        globalConfig.Item = "test.txt"

        mockSvc := &mockS3Client{}

        err = RestoreItem(mockSvc, &globalConfig.Bucket, &globalConfig.Item, &globalConfig.Days)
        if err != nil {
            t.Log("Could not restore " + globalConfig.Item + " to " + globalConfig.Bucket)
            t.Fatal(err)
        }
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := s3.New(sess)

        err = RestoreItem(svc, &globalConfig.Bucket, &globalConfig.Item, &globalConfig.Days)
        if err != nil {
            t.Log("Could not restore " + globalConfig.Item + " to " + globalConfig.Bucket)
            t.Fatal(err)
        }
    }

    t.Log("Restored " + globalConfig.Item + " to " + globalConfig.Bucket)
}
