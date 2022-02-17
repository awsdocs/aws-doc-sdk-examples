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

func (m *mockIAMClient) DeleteUser(input *iam.DeleteUserInput) (*iam.DeleteUserOutput, error) {
    // Check that required inputs exist
    if input.UserName == nil || *input.UserName == "" {
        return nil, errors.New("The DeleteUserInput.UserName was nil or an empty string")
    }

    resp := iam.DeleteUserOutput{}
    return &resp, nil
}

func TestCreateUser(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    userName := "test-user"

    mockSvc := &mockIAMClient{}

    err := RemoveUser(mockSvc, &userName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted user " + userName)
}
