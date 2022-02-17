// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[codebuild.go.list_builds]
package main

// snippet-start:[codebuild.go.list_builds.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/codebuild"
)
// snippet-end:[codebuild.go.list_builds.imports]

// GetBuilds retrieves a list of AWS CodeBuild builds
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of the builds and nil
//     Otherwise, nil and an error from the call to ListBuilds or BatchGetBuilds
func GetBuilds(sess *session.Session) (*codebuild.BatchGetBuildsOutput, error) {
    // snippet-start:[codebuild.go.list_builds.call]
    svc := codebuild.New(sess)
    // Get the list of builds
    names, err := svc.ListBuilds(&codebuild.ListBuildsInput{
        SortOrder: aws.String("ASCENDING"),
    })
    // snippet-end:[codebuild.go.list_builds.call]
    if err != nil {
        return nil, err
    }

    // snippet-start:[codebuild.go.list_builds.batch]
    builds, err := svc.BatchGetBuilds(&codebuild.BatchGetBuildsInput{
        Ids: names.Ids,
    })
    // snippet-end:[codebuild.go.list_builds.batch]
    if err != nil {
        return nil, err
    }

    return builds, nil
}

func main() {
    // snippet-start:[codebuild.go.list_builds.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[codebuild.go.list_builds.session]

    builds, err := GetBuilds(sess)
    if err != nil {
        fmt.Println("Got error getting builds:")
        fmt.Println(err)
        return
    }

    // snippet-start:[codebuild.go.list_builds.display]
    for _, build := range builds.Builds {
        fmt.Printf("Project: %s\n", aws.StringValue(build.ProjectName))
        fmt.Printf("Phase:   %s\n", aws.StringValue(build.CurrentPhase))
        fmt.Printf("Status:  %s\n", aws.StringValue(build.BuildStatus))
        fmt.Println("")
    }
    // snippet-end:[codebuild.go.list_builds.display]
}
// snippet-end:[codebuild.go.list_builds]
