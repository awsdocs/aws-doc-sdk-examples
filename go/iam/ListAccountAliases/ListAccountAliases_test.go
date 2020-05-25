// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
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
func (m *mockIAMClient) ListAccountAliases(input *iam.ListAccountAliasesInput) (*iam.ListAccountAliasesOutput, error) {
    // No required inputs
    resp := iam.ListAccountAliasesOutput{}
    return &resp, nil
}

func TestListAccountAliases(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    maxItems := int64(10)

    mockSvc := &mockIAMClient{}

    _, err := GetAccountAliases(mockSvc, &maxItems)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved the account aliases")
}
