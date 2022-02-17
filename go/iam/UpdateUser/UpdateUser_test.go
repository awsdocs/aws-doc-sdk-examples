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

func (m *mockIAMClient) UpdateUser(input *iam.UpdateUserInput) (*iam.UpdateUserOutput, error) {
    // Check that required inputs exist
    if input.UserName == nil || *input.UserName == "" || input.NewUserName == nil || *input.NewUserName == "" {
        return nil, errors.New("The UpdateUserInput.UserName or UpdateUserInput.NewUserName was nil or an empty string")
    }

    resp := iam.UpdateUserOutput{}
    return &resp, nil
}

func TestCreateUser(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    userName := "test-user"
    newName := "new-test-user"

    mockSvc := &mockIAMClient{}

    err := RenameUser(mockSvc, &userName, &newName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Renamed user " + userName + " to " + newName)
}
