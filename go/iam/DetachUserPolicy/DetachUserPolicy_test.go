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

func (m *mockIAMClient) DetachRolePolicy(input *iam.DetachRolePolicyInput) (*iam.DetachRolePolicyOutput, error) {
    // Check that required inputs exist
    if input.PolicyArn == nil || *input.PolicyArn == "" || input.RoleName == nil || *input.RoleName == "" {
        return nil, errors.New("DetacheRolePolicyInput.PolicyArn or DetachRolePolicyInput.RoleName is nil or an empty string")
    }

    resp := iam.DetachRolePolicyOutput{}
    return &resp, nil
}

func TestDetachRolePolicy(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    roleName := "test-role"

    mockSvc := &mockIAMClient{}

    err := DetachDynamoFullPolicy(mockSvc, &roleName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Detached DynamoDB full-access policy to role " + roleName)
}
