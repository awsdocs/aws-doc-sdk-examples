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

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3iface"

    "github.com/google/uuid"
)

// Define a mock struct to be used in your unit tests of myFunc.
type mockS3Client struct {
    s3iface.S3API
}

func (m *mockS3Client) PutObjectAcl(*s3.PutObjectAclInput) (*s3.PutObjectAclOutput, error) {
    resp := s3.PutObjectAclOutput{
        RequestCharged: aws.String(""),
    }

    return &resp, nil
}

type Config struct {
    Bucket  string `json:"Name"`
    Key     string `json:"Key"`
    Address string `json:"Address"`
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
    t.Log("Key:        " + globalConfig.Key)
    t.Log("Address:    " + globalConfig.Address)

    return nil
}

func TestPubObjectAcl(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.Bucket == "" || globalConfig.Key == "" || globalConfig.Address == "" {
        // mock resources
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()
        globalConfig.Key = "random"
        globalConfig.Address = "somebody@somewhere"

        mockSvc := &mockS3Client{}

        err = PutObjectACL(mockSvc, &globalConfig.Bucket, &globalConfig.Key, &globalConfig.Address)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Congratulations. You gave user with email address", globalConfig.Address, "read permission to bucket", globalConfig.Bucket, "object", globalConfig.Key)
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := s3.New(sess)

        err = PutObjectACL(svc, &globalConfig.Bucket, &globalConfig.Key, &globalConfig.Address)
        if err != nil {

            t.Fatal(err)
        }

        t.Log("Congratulations. You gave user with email address", globalConfig.Address, "read permission to bucket", globalConfig.Bucket, "object", globalConfig.Key)
    }

}
