// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/codebuild"
)

// Lists the CodeBuild builds for all projects in the region configured in the shared config
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create CodeBuild service client
	svc := codebuild.New(sess)

	// Get the list of builds
	names, err := svc.ListBuilds(&codebuild.ListBuildsInput{SortOrder: aws.String("ASCENDING")})

	if err != nil {
		fmt.Println("Got error listing builds: ", err)
		os.Exit(1)
	}

	// Get information about each build
	builds, err := svc.BatchGetBuilds(&codebuild.BatchGetBuildsInput{Ids: names.Ids})

	if err != nil {
		fmt.Println("Got error getting builds: ", err)
		os.Exit(1)
	}

	for _, build := range builds.Builds {
		fmt.Printf("Project: %s\n", aws.StringValue(build.ProjectName))
		fmt.Printf("Phase:   %s\n", aws.StringValue(build.CurrentPhase))
		fmt.Printf("Status:  %s\n", aws.StringValue(build.BuildStatus))
		fmt.Println("")
	}
}
