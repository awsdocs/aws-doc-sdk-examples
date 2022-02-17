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

// Then for every *real* call you make in the example:
func (m *mockIAMClient) CreateUser(input *iam.CreateUserInput) (*iam.CreateUserOutput, error) {
    // Check that required inputs exist
    if input.UserName == nil || *input.UserName == "" {
        return nil, errors.New("The CreateUserInput.UserName was nil or an empty string")
    }

    resp := iam.CreateUserOutput{}
    return &resp, nil
}

func TestCreateUser(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    userName := "test-user"

    mockSvc := &mockIAMClient{}

    err := MakeUser(mockSvc, &userName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created user " + userName)
}
