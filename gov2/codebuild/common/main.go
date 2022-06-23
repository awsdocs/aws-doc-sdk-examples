package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/codebuild"
	"github.com/aws/aws-sdk-go-v2/service/codebuild/types"
)

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
