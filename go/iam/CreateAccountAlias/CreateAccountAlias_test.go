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

func (m *mockIAMClient) CreateAccountAlias(input *iam.CreateAccountAliasInput) (*iam.CreateAccountAliasOutput, error) {
    // Check that required inputs exist
    if input.AccountAlias == nil || *input.AccountAlias == "" {
        return nil, errors.New("CreateAccountAliasInput.AccountAlias cannot be nil or an empty string")
    }

    resp := iam.CreateAccountAliasOutput{}
    return &resp, nil
}

func TestCreateAccountAlias(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    alias := "test-alias"

    mockSvc := &mockIAMClient{}

    err := MakeAccountAlias(mockSvc, &alias)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created account alias " + alias)
}
