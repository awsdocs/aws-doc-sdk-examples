// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)

// Define a mock struct to use in unit tests
type mockIAMClient struct {
    iamiface.IAMAPI
}

func (m *mockIAMClient) DeleteAccessKey(input *iam.DeleteAccessKeyInput) (*iam.DeleteAccessKeyOutput, error) {
    // Check that required inputs exist
    if input.AccessKeyId == nil || *input.AccessKeyId == "" || input.UserName == nil || *input.UserName == "" {
        return nil, errors.New("DeleteAccessKeyInput.AccessKeyId or DeleteAccessKeyInput.UserName is nil or an empty string")
    }

    resp := iam.DeleteAccessKeyOutput{}
    return &resp, nil
}

func TestDeleteAccessKey(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    keyID := "test-key"
    userName := "test-user"

    mockSvc := &mockIAMClient{}

    err := RemoveAccessKey(mockSvc, &keyID, &userName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted key with ID " + keyID + " from user " + userName)
}
