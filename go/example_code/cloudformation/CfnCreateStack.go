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
    "github.com/aws/aws-sdk-go/service/cloudformation"

    "flag"
    "fmt"
    "os"
)

// Creates the stack from the template in the specified S3 Bucket in the region
//
// Usage:
//    go run CfnCreateStack.go -b BUCKET_NAME [-d] [-r REGION] -s STACK_NAME -t TEMPLATE_NAME
func main() {
    bucketPtr := flag.String("b", "", "The bucket from which the template is retrieved")
    nukePtr := flag.Bool("d", true, "Whether to delete the stack when we are done")
    regionPtr := flag.String("r", "us-west-2", "The region containing the bucket")
    stackPtr := flag.String("s", "", "The name of the stack")
    templatePtr := flag.String("t", "", "The CloudFormation template")

    flag.Parse()

    bucket := *bucketPtr
    nuke := *nukePtr
    region := *regionPtr
    stack := *stackPtr
    template := *templatePtr

    // Make sure we have a bucket, template, and stack name
    if bucket == "" || template == "" || stack == "" {
        fmt.Println("You must supply a bucket name, template name, and stack name")
        os.Exit(1)
    }

    // Construct the URL for the template
    // The format is:
    // https://BUCKET.s3.REGION.amazonaws.com/TEMPLATE
    url := "https://" + bucket + ".s3." + region + ".amazonaws.com/" + template

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSession(&aws.Config{Region: regionPtr}))

    // Create CloudFormation client in region
    svc := cloudformation.New(sess)

    // Create stack
    fmt.Println("Creating the stack")

    input := &cloudformation.CreateStackInput{TemplateURL: aws.String(url), StackName: stackPtr}

    _, err := svc.CreateStack(input)
    if err != nil {
        fmt.Println("Got error creating stack:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Waiting for the stack to be created")

    // Wait until stack is created
    desInput := &cloudformation.DescribeStacksInput{StackName: stackPtr}
    err = svc.WaitUntilStackCreateComplete(desInput)
    if err != nil {
        fmt.Println("Got error waiting for stack to be created")
        fmt.Println(err)
        os.Exit(1)
    }

    // Delete the stack as it was just a test
    if nuke {
        fmt.Println("Deleting the stack " + stack)

        delInput := &cloudformation.DeleteStackInput{StackName: stackPtr}
        svc.DeleteStack(delInput)
    }

    fmt.Println("Done.")
}
