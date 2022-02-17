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
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3iface"
    "github.com/google/uuid"
)

// Define a mock struct to use in unit tests
type mockS3Client struct {
    s3iface.S3API
}

func (m *mockS3Client) CreateBucket(input *s3.CreateBucketInput) (*s3.CreateBucketOutput, error) {
    if input.Bucket == nil || *input.Bucket == "" {
        return nil, errors.New("The bucket name is nil or empty")
    }

    resp := s3.CreateBucketOutput{}

    return &resp, nil
}

func (m *mockS3Client) HeadBucket(input *s3.HeadBucketInput) (*s3.HeadBucketOutput, error) {
    if input.Bucket == nil || *input.Bucket == "" {
        return nil, errors.New("The bucket name is nil or empty")
    }

    resp := s3.HeadBucketOutput{}

    return &resp, nil
}

func (m *mockS3Client) GetBucketAcl(input *s3.GetBucketAclInput) (*s3.GetBucketAclOutput, error) {
    if input.Bucket == nil || *input.Bucket == "" {
        return nil, errors.New("The bucket name is nil or empty")
    }

    g1 := &s3.Grant{
        Grantee: &s3.Grantee{
            DisplayName: aws.String("me"),
            ID:          aws.String("123456789"),
            Type:        aws.String("CanonicalUser"),
        },
        Permission: aws.String("FULL_CONTROL"),
    }

    g2 := &s3.Grant{
        Grantee: &s3.Grantee{
            Type: aws.String("Group"),
            URI:  aws.String("http://acs.amazonaws.com/groups/global/AllUsers"),
        },
        Permission: aws.String("READ"),
    }

    grants := []*s3.Grant{g1, g2}

    resp := s3.GetBucketAclOutput{
        Grants: grants,
        Owner: &s3.Owner{
            DisplayName: aws.String("me"),
            ID:          aws.String("123456789"),
        },
    }

    return &resp, nil
}

func (m *mockS3Client) PutBucketAcl(input *s3.PutBucketAclInput) (*s3.PutBucketAclOutput, error) {
    if input.Bucket == nil || *input.Bucket == "" {
        return nil, errors.New("The bucket name is nil or empty")
    }

    resp := s3.PutBucketAclOutput{}

    return &resp, nil
}

func (m *mockS3Client) DeleteBucket(input *s3.DeleteBucketInput) (*s3.DeleteBucketOutput, error) {
    if input.Bucket == nil || *input.Bucket == "" {
        return nil, errors.New("The bucket name is nil or empty")
    }

    resp := s3.DeleteBucketOutput{}

    return &resp, nil
}

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

func TestS3CrudOps(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.Bucket == "" {
        // mock resources
        id := uuid.New()
        globalConfig.Bucket = "test-bucket-" + id.String()

        mockSvc := &mockS3Client{}

        err = MakeBucket(mockSvc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = GetBucket(mockSvc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = UpdateBucket(mockSvc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = RemoveBucket(mockSvc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Successfully ran CRUD operations on bucket " + globalConfig.Bucket)
    } else {
        sess := session.Must(session.NewSessionWithOptions(session.Options{
            SharedConfigState: session.SharedConfigEnable,
        }))

        svc := s3.New(sess)

        err = MakeBucket(svc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = GetBucket(svc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = UpdateBucket(svc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        err = RemoveBucket(svc, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Successfully ran CRUD operations on bucket " + globalConfig.Bucket)
    }
}
