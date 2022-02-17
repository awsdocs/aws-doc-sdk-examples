// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) CreateAccessKey(input *iam.CreateAccessKeyInput) (*iam.CreateAccessKeyOutput, error) {
    // Check that required inputs exist
    if input.UserName == nil || *input.UserName == "" {
        return nil, errors.New("The CreateAccessKeyInput.Username is nil or ")
    }

    resp := iam.CreateAccessKeyOutput{
        AccessKey: &iam.AccessKey{
            AccessKeyId:     aws.String("abc123xyz"),
            SecretAccessKey: aws.String("stu789"),
        },
    }

    return &resp, nil
}

func TestCreateAccessKey(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    userName := "test-user"

    mockSvc := &mockIAMClient{}

    result, err := MakeAccessKey(mockSvc, &userName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created new access key with ID: " + *result.AccessKey.AccessKeyId + " and secret key: " + *result.AccessKey.SecretAccessKey)
}
