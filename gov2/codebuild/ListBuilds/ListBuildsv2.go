// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[codebuild.go-v2.ListBuilds]
package main

import (
	"context"
	"fmt"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/codebuild"
	"github.com/aws/aws-sdk-go-v2/service/codebuild/types"
)

// CodeBuildGetBuildsAPI defines the interface for the ListBuilds and BatchGetBuilds functions.
// We use this interface to test the functions using a mocked service.
type CodeBuildGetBuildsAPI interface {
	BatchGetBuilds(ctx context.Context, params *codebuild.BatchGetBuildsInput, optFns ...func(*codebuild.Options)) (*codebuild.BatchGetBuildsOutput, error)
	ListBuilds(ctx context.Context, params *codebuild.ListBuildsInput, optFns ...func(*codebuild.Options)) (*codebuild.ListBuildsOutput, error)
}

// GetBuilds retrieves a list of AWS CodeBuild builds by Ids.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call which contains list of code build ids to fetch.
// Output:
//     If success, a BatchGetBuildsOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to BatchGetBuilds.
func GetBuilds(c context.Context, api CodeBuildGetBuildsAPI, input *codebuild.BatchGetBuildsInput) (*codebuild.BatchGetBuildsOutput, error) {
	return api.BatchGetBuilds(c, input)
}

// ListBuilds retrieves a list of AWS CodeBuild builds.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call which contains list of code build ids to fetch.
// Output:
//     If success, a ListBuildsOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListBuilds.
func ListBuilds(c context.Context, api CodeBuildGetBuildsAPI, input *codebuild.ListBuildsInput) (*codebuild.ListBuildsOutput, error) {
	return api.ListBuilds(c, input)
}

func main() {
	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := codebuild.NewFromConfig(cfg)

	listBuildInput := &codebuild.ListBuildsInput{
		SortOrder: types.SortOrderTypeAscending,
	}

	listBuilds, err := ListBuilds(context.TODO(), client, listBuildInput)

	if err != nil {
		fmt.Println("Got an error listing builds:")
		fmt.Println(err)
		return
	}

	input := &codebuild.BatchGetBuildsInput{
		Ids: listBuilds.Ids,
	}

	builds, err := GetBuilds(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error getting builds:")
		fmt.Println(err)
		return
	}

	for _, build := range builds.Builds {
		fmt.Printf("Project: %s\n", *build.ProjectName)
		fmt.Printf("Phase:   %s\n", *build.CurrentPhase)
		fmt.Printf("Status:  %s\n", build.BuildStatus)
		fmt.Println("")
	}
}

// snippet-end:[codebuild.go-v2.ListBuilds]
