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

func (m *mockIAMClient) CreatePolicy(input *iam.CreatePolicyInput) (*iam.CreatePolicyOutput, error) {
    //     Check that required inputs exist
    if input.PolicyDocument == nil || *input.PolicyDocument == "" || input.PolicyName == nil || *input.PolicyName == "" {
        return nil, errors.New("CreatePolicyInput.PolicyDocument or CreatePolicyInput.PolicyName is nil or an empty string")
    }

    resp := iam.CreatePolicyOutput{}
    return &resp, nil
}

func TestCreatePolicy(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockIAMClient{}

    policyName := "test-policy"

    err := MakePolicy(mockSvc, &policyName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created policy " + policyName)
}
