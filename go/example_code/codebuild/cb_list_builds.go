//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Displays information about your AWS CodeBuild project builds.]
//snippet-keyword:[AWS CodeBuild]
//snippet-keyword:[ListBuilds function]
//snippet-keyword:[Go]
//snippet-service:[codebuild]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
