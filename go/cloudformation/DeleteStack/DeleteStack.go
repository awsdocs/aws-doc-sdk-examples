// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[cfn.go.delete_stack]
package main

// snippet-start:[cfn.go.delete_stack.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudformation"
    "github.com/aws/aws-sdk-go/service/cloudformation/cloudformationiface"
)
// snippet-end:[cfn.go.delete_stack.imports]

// RemoveStack deletes an AWS CloudFormation stack
// Inputs:
//     svc is an AWS CloudFormation service client
//     stackName is the name of the new stack
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteStack
func RemoveStack(svc cloudformationiface.CloudFormationAPI, stackName *string) error {
    // snippet-start:[cfn.go.delete_stack.call]
    _, err := svc.DeleteStack(&cloudformation.DeleteStackInput{
        StackName: stackName,
    })
    // snippet-end:[cfn.go.delete_stack.call]

    return err
}

func main() {
    // snippet-start:[cfn.go.delete_stack.args]
    stackName := flag.String("n", "", "The name of the stack to create or delete")
    flag.Parse()

    if *stackName == "" {
        fmt.Println("You must supply a stack name (-s STACK-NAME)")
        return
    }
    // snippet-end:[cfn.go.delete_stack.args]

    // snippet-start:[cfn.go.delete_stack.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := cloudformation.New(sess)
    // snippet-end:[cfn.go.delete_stack.session]

    err := RemoveStack(svc, stackName)
    if err != nil {
        fmt.Println("Got an error deleting stack " + *stackName)
        return
    }

    // snippet-start:[cfn.go.delete_stack.wait]
    err = svc.WaitUntilStackDeleteComplete(&cloudformation.DescribeStacksInput{
        StackName: stackName,
    })
    if err != nil {
        fmt.Println("Got an error waiting for stack to be deleted")
        return
    }
    // snippet-end:[cfn.go.delete_stack.wait]

    fmt.Println("Deleted stack " + *stackName)
}
// snippet-end:[cfn.go.delete_stack]
