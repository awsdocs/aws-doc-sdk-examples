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

func (m *mockIAMClient) ListAccessKeys(input *iam.ListAccessKeysInput) (*iam.ListAccessKeysOutput, error) {
    // Check that required inputs exist
    if input.UserName == nil || *input.UserName == "" {
        return nil, errors.New("The ListAccessKeysInput.UserName was nil or an empty string")
    }

    resp := iam.ListAccessKeysOutput{
        AccessKeyMetadata: []*iam.AccessKeyMetadata{
            &iam.AccessKeyMetadata{
                AccessKeyId: aws.String("abc123xyx"),
                Status:      aws.String("Active"),
            },
        },
    }

    return &resp, nil
}

func TestListAccessKeys(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    maxItems := int64(10)
    userName := "test-user"

    mockSvc := &mockIAMClient{}

    _, err := GetAccessKeys(mockSvc, &maxItems, &userName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Got access keys for " + userName)
}
