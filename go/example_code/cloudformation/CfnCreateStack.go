//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
    "github.com/aws/aws-sdk-go/service/cloudformation"

    "flag"
    "fmt"
    "os"
)

func main() {
    // Set stack name, template url
    stackName := "my-groovy-stack"
    templateUrl := "https://my-groovy-bucket.s3.us-west-2.amazonaws.com/my-groovy-template"

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create CloudFormation client in region
    svc := cloudformation.New(sess)

    input := &cloudformation.CreateStackInput{TemplateURL: aws.String(templateUrl), StackName: aws.String(stackName)}

    _, err := svc.CreateStack(input)
    if err != nil {
        fmt.Println("Got error creating stack:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Waiting for stack to be created")

    // Wait until stack is created
    desInput := &cloudformation.DescribeStacksInput{StackName: aws.String(stackName)}
    err = svc.WaitUntilStackCreateComplete(desInput)
    if err != nil {
        fmt.Println("Got error waiting for stack to be created")
        fmt.Println(err)
        os.Exit(1)
    }

    fmt.Println("Created stack " + stackName)
}
