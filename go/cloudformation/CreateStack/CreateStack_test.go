// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/cloudformation"
    "github.com/aws/aws-sdk-go/service/cloudformation/cloudformationiface"
)

// Define a mock struct to use in unit tests
type mockCFNClient struct {
    cloudformationiface.CloudFormationAPI
}

func (m *mockCFNClient) CreateStack(input *cloudformation.CreateStackInput) (*cloudformation.CreateStackOutput, error) {
    // Check that required inputs exist
    if input.StackName == nil || *input.StackName == "" || input.TemplateBody == nil || *input.TemplateBody == "" {
        return nil, errors.New("CreateStackInput.StackName or CreateStackInput.TemplateBody is nil or an empty string")
    }

    resp := cloudformation.CreateStackOutput{}
    return &resp, nil
}

func TestCreateStack(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    stackName := "test-stack"
    templateBody := "{}"

    mockSvc := &mockCFNClient{}

    err := MakeStack(mockSvc, &stackName, &templateBody)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created stack " + stackName)
}
