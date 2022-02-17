// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)

// Define a mock struct to use in unit tests
type mockLambdaClient struct {
    lambdaiface.LambdaAPI
}

func (m *mockLambdaClient) AddPermission(input *lambda.AddPermissionInput) (*lambda.AddPermissionOutput, error) {
    // Check that required inputs exist
    if input.FunctionName == nil || *input.FunctionName == "" ||
        input.SourceArn == nil || *input.SourceArn == "" {
        return nil, errors.New("AddPermissionInput.FunctionName or AddPermissionInput.SourceArn is nil or an empty string")
    }

    resp := lambda.AddPermissionOutput{}
    return &resp, nil
}

func TestConfigureFunction(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    functionName := "test-function-name"
    sourceARN := "test-source-arn"

    mockSvc := &mockLambdaClient{}

    err := AddPerm(mockSvc, &functionName, &sourceARN)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Configured function " + functionName + " to accept notifications from S3 bucket with ARN " + sourceARN)
}
