// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "errors"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/service/codebuild"
    "github.com/aws/aws-sdk-go/service/codebuild/codebuildiface"
)

// Define a mock struct to use in unit tests
type mockCodeBuildClient struct {
    codebuildiface.CodeBuildAPI
}

func (m *mockCodeBuildClient) StartBuild(input *codebuild.StartBuildInput) (*codebuild.StartBuildOutput, error) {
    // Check that required inputs exist
    if input.ProjectName == nil || *input.ProjectName == "" {
        return nil, errors.New("StartBuildInput.ProjectName is nil or an empty string")
    }

    resp := codebuild.StartBuildOutput{}
    return &resp, nil
}

func TestBuildProject(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    // mock resource
    project := "test-project"

    mockSvc := &mockCodeBuildClient{}

    err := BldProject(mockSvc, &project)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Built project " + project)
}
