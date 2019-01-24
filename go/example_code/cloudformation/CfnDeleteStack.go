// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Deletes a CloudFormation stack.]
// snippet-keyword:[AWS CloudFormation]
// snippet-keyword:[DeleteStack function]
// snippet-keyword:[WaitUntilStackDeleteComplete function]
// snippet-keyword:[Go]
// snippet-service:[cloudformation]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

// Deletes the stack
//
// Usage:
//    go run CfnDeleteStack.go [-r REGION] -s STACK_NAME
func main() {
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create CloudFormation client in region
    svc := cloudformation.New(sess)

    // Delete the stack as it was just a test
    delInput := &cloudformation.DeleteStackInput{StackName: aws.String("my-groovy-stack")}
    _, err := svc.DeleteStack(delInput)
    if err != nil {
        fmt.Println("Got error deleting stack:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Wait until stack is created
    desInput := &cloudformation.DescribeStacksInput{StackName: aws.String("my-groovy-stack")}
    err = svc.WaitUntilStackDeleteComplete(desInput)
    if err != nil {
        fmt.Println("Got error waiting for stack to be deleted")
        fmt.Println(err)
        os.Exit(1)
    }

    fmt.Println("Deleted stack my-groovy-stack")
}
