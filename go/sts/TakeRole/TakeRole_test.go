// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/sts"
    "github.com/aws/aws-sdk-go/service/sts/stsiface"
)

// Define a mock struct to use in unit tests
type mockSTSClient struct {
    stsiface.STSAPI
}

func (m *mockSTSClient) AssumeRole(input *sts.AssumeRoleInput) (*sts.AssumeRoleOutput, error) {
    // Check that required inputs exist
    if input.RoleArn == nil || *input.RoleArn == "" || input.RoleSessionName == nil || *input.RoleSessionName == "" {
        return nil, errors.New("AssumeRoleInput.RoleArn or .RoleSessionName is nil of an empty string")
    }

    resp := sts.AssumeRoleOutput{
        AssumedRoleUser: &sts.AssumedRoleUser{
            Arn:           aws.String("test-assumed-role-user-ARN"),
            AssumedRoleId: aws.String("test-assumed-role-user-ID"),
        },
    }
    return &resp, nil
}

func TestTakeRole(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    roleARN := "test-role-ARN"
    sessionName := "test-session-name"

    mockSvc := &mockSTSClient{}

    result, err := TakeRole(mockSvc, &roleARN, &sessionName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Assumed role user ARN: " + *result.AssumedRoleUser.Arn)
    t.Log("Assumed role user ID:  " + *result.AssumedRoleUser.AssumedRoleId)
}
