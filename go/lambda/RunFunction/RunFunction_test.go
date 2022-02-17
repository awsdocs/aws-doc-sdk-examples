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

func (m *mockLambdaClient) Invoke(input *lambda.InvokeInput) (*lambda.InvokeOutput, error) {
    // Check that required inputs exist
    if input.FunctionName == nil || *input.FunctionName == "" || input.Payload == nil {
        return nil, errors.New("InvokeInput.FunctionName is nil or an empty string, or InvokeInput.Payload is nil")
    }

    resp := lambda.InvokeOutput{}
    return &resp, nil
}

func TestRunFunction(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    maxItems := 10
    function := "test-function"

    mockSvc := &mockLambdaClient{}

    _, err := CallFunction(mockSvc, &maxItems, &function)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Successfully called Lambda function " + function)
}
