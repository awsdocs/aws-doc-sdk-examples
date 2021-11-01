// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/codebuild"
	"github.com/aws/aws-sdk-go-v2/service/codebuild/types"
)

type CodeBuildGetBuildsImpl struct{}

func (dt CodeBuildGetBuildsImpl) ListBuilds(ctx context.Context, params *codebuild.ListBuildsInput, optFns ...func(*codebuild.Options)) (*codebuild.ListBuildsOutput, error) {
	output := &codebuild.ListBuildsOutput{
		Ids: []string{"id1"},
	}
	return output, nil
}

func (dt CodeBuildGetBuildsImpl) BatchGetBuilds(ctx context.Context, params *codebuild.BatchGetBuildsInput, optFns ...func(*codebuild.Options)) (*codebuild.BatchGetBuildsOutput, error) {
	output := &codebuild.BatchGetBuildsOutput{
		Builds: []types.Build{{
			BuildStatus: "SUCCESS",
			ProjectName: aws.String("Test"),
		}},
	}
	return output, nil
}

func TestListListBuilds(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	api := &CodeBuildGetBuildsImpl{}

	input := &codebuild.ListBuildsInput{}

	resp, err := ListBuilds(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	for id := range resp.Ids {
		t.Log("Build Id: ", id)
	}
}

func TestListGetBuilds(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	api := &CodeBuildGetBuildsImpl{}

	input := &codebuild.BatchGetBuildsInput{}

	builds, err := GetBuilds(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	for _, build := range builds.Builds {
		t.Log("Project:", *build.ProjectName)
		t.Log("Status: ", build.BuildStatus)
	}
}
