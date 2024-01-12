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

// Builds a CodeBuild project in the region configured in the shared config
func main() {
	// Requires one argument, the name of the project.
	if len(os.Args) != 2 {
		fmt.Println("Project name required!")
		os.Exit(1)
	}

	project := os.Args[1]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create CodeBuild service client
	svc := codebuild.New(sess)

	// Build the project
	_, err = svc.StartBuild(&codebuild.StartBuildInput{ProjectName: aws.String(project)})
	if err != nil {
		fmt.Println("Got error building project: ", err)
		os.Exit(1)
	}

	fmt.Printf("Started build for project %q\n", project)
}
