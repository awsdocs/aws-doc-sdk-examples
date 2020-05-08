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

func (m *mockIAMClient) DeleteAccountAlias(input *iam.DeleteAccountAliasInput) (*iam.DeleteAccountAliasOutput, error) {
    // Check that required inputs exist
    if input.AccountAlias == nil || *input.AccountAlias == "" {
        return nil, errors.New("DeleteAccountAliasInput.AccountAlias cannot be nil or an empty string")
    }

    resp := iam.DeleteAccountAliasOutput{}
    return &resp, nil
}

func TestDeleteAccountAlias(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    alias := "test-alias"

    mockSvc := &mockIAMClient{}

    err := RemoveAccountAlias(mockSvc, &alias)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted account alias " + alias)
}
