// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)

// Define a mock struct to use in unit tests
type mockLambdaClient struct {
    lambdaiface.LambdaAPI
}

func (m *mockLambdaClient) ListFunctions(input *lambda.ListFunctionsInput) (*lambda.ListFunctionsOutput, error) {
    resp := lambda.ListFunctionsOutput{}
    return &resp, nil
}

func TestShowFunctions(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    mockSvc := &mockLambdaClient{}

    _, err := GetFunctions(mockSvc)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved the list of functions")
}
