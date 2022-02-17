// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[codebuild.go.list_projects]
package main

// snippet-start:[codebuild.go.list_projects.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/codebuild"
)
// snippet-end:[codebuild.go.list_projects.imports]

// GetProjects retrieves a list of your AWS CodeBuild projects
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of the projects and nil
//     Otherwise, nil and an error from the call to ListProjects
func GetProjects(sess *session.Session) (*codebuild.ListProjectsOutput, error) {
    // snippet-start:[codebuild.go.list_projects.call]
    svc := codebuild.New(sess)

    // Get the list of projects
    result, err := svc.ListProjects(
        &codebuild.ListProjectsInput{
            SortBy:    aws.String("NAME"),
            SortOrder: aws.String("ASCENDING"),
        })
    // snippet-end:[codebuild.go.list_projects.call]

    return result, err
}

func main() {
    // snippet-start:[codebuild.go.list_projects.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[codebuild.go.list_projects.session]

    result, err := GetProjects(sess)
    if err != nil {
        fmt.Println("Got an error listing projects:")
        fmt.Println(err)
        return
    }

    // snippet-start:[codebuild.go.list_projects.display]
    for _, p := range result.Projects {
        fmt.Println(*p)
    }
    // snippet-end:[codebuild.go.list_projects.display]
}
// snippet-end:[codebuild.go.list_projects]
