// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[codebuild.go.build_project]
package main

// snippet-start:[codebuild.go.build_project.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/codebuild"
    "github.com/aws/aws-sdk-go/service/codebuild/codebuildiface"
)
// snippet-end:[codebuild.go.build_project.imports]

// BldProject builds a CodeBuild project
// Inputs:
//     svc is a CodeBuild service client
//     proj is the name of the project
// Output:
//     If success, nil
//     Otherwise, an error from the call to
func BldProject(svc codebuildiface.CodeBuildAPI, project *string) error {
    // snippet-start:[codebuild.go.build_project.call]
    _, err := svc.StartBuild(&codebuild.StartBuildInput{
        ProjectName: project,
    })
    // snippet-end:[codebuild.go.build_project.call]
    if err != nil {
        return err
    }

    return nil
}
func main() {
    // snippet-start:[codebuild.go.build_project.args]
    project := flag.String("p", "", "The name of the project")
    flag.Parse()

    if *project == "" {
        fmt.Println("You must supply the name of the project to build")
        return
    }
    // snippet-end:[codebuild.go.build_project.args]

    // snippet-start:[codebuild.go.build_project.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := codebuild.New(sess)
    // snippet-end:[codebuild.go.build_project.session]

    err := BldProject(svc, project)
    if err != nil {
        fmt.Println("Got an error building the project")
        fmt.Println(err)
        return
    }

    fmt.Println("Started build for project ", *project)
}
// snippet-end:[codebuild.go.build_project]
