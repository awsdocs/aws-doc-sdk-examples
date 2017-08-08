/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/codebuild"
	"fmt"
	"os"
)

// Lists a CodeBuild projects in the region configured in the shared config
//
// Usage:
//    go run cb_list_projects
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create CodeBuild service client
	svc := codebuild.New(sess)

	// Get the list of projects
	result, err := svc.ListProjects(
		&codebuild.ListProjectsInput{
			SortBy:    aws.String("NAME"),
			SortOrder: aws.String("ASCENDING", )})

	if err != nil {
		exitErrorf("Got error listing projects: %v", err)
	}

	for _, p := range result.Projects {
		fmt.Println(*p)
	}
}

func exitErrorf(msg string, args ...interface {}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
