// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/lambda/lambdaiface"
)

// Define a mock struct to use in unit tests
type mockLambdaClient struct {
    lambdaiface.LambdaAPI
}

func (m *mockLambdaClient) CreateFunction(input *lambda.CreateFunctionInput) (*lambda.FunctionConfiguration, error) {
    // Check that required inputs exist
    if input.Code == nil ||
        input.Code.S3Bucket == nil || *input.Code.S3Bucket == "" ||
        input.Code.S3Key == nil || *input.Code.S3Key == "" ||
        input.Code.S3ObjectVersion == nil ||
        input.Code.ZipFile == nil ||
        input.FunctionName == nil || *input.FunctionName == "" ||
        input.Handler == nil || *input.Handler == "" ||
        input.Role == nil || *input.Role == "" ||
        input.Runtime == nil || *input.Runtime == "" {
        return nil, errors.New("A required input value is nil or an empty string where not allowed")
    }

    resp := lambda.FunctionConfiguration{
        FunctionArn: aws.String("test-lambda-arn"),
    }
    return &resp, nil
}

func TestMakeFunction(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resources
    zipFile := "testFile" // without the .zip
    bucket := "test-BUCKET"
    function := "test-FUNCTION-NAME"
    handler := "test-HANDLER"
    arn := "test-ROLE-ARN"
    runtime := "test-RUNTIME"

    mockSvc := &mockLambdaClient{}

    result, err := MakeFunction(mockSvc, &zipFile, &bucket, &function, &handler, &arn, &runtime)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Function ARN: " + *result.FunctionArn)
}
