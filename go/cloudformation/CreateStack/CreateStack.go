// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[cfn.go.create_stack]
package main

// snippet-start:[cfn.go.create_stack.imports]
import (
    "flag"
    "fmt"
    "io/ioutil"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudformation"
    "github.com/aws/aws-sdk-go/service/cloudformation/cloudformationiface"
)
// snippet-end:[cfn.go.create_stack.imports]

// MakeStack creates a CloudFormation stack
// Inputs:
//     svc is a CloudFormation service client
//     stackName is the name of the new stack
//     templateBody is the contents of the CloudFormation template
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateStack
func MakeStack(svc cloudformationiface.CloudFormationAPI, stackName, templateBody *string) error {
    // snippet-start:[cfn.go.create_stack.call]
    _, err := svc.CreateStack(&cloudformation.CreateStackInput{
        TemplateBody: templateBody,
        StackName:    stackName,
    })
    // snippet-end:[cfn.go.create_stack.call]

    return err
}

func main() {
    // snippet-start:[cfn.go.create_stack.args]
    stackName := flag.String("n", "", "The name of the stack to create or delete")
    templateFile := flag.String("t", "", "The name of the file containing the CloudFormation template")
    flag.Parse()

    if *stackName == "" || *templateFile == "" {
        fmt.Println("You must supply a stack name and template file name (-s STACK-NAME -t TEMPLATE-FILE)")
        return
    }
    // snippet-end:[cfn.go.create_stack.args]

    // Open file template
    // Get entire file as a string
    // snippet-start:[cfn.go.create_stack.read_file]
    content, err := ioutil.ReadFile(*templateFile)
    if err != nil {
        return
    }

    // Convert []byte to string
    templateBody := string(content)
    // snippet-end:[cfn.go.create_stack.read_file]

    // snippet-start:[cfn.go.create_stack.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := cloudformation.New(sess)
    // snippet-end:[cfn.go.create_stack.session]

    err = MakeStack(svc, stackName, &templateBody)
    if err != nil {
        fmt.Println("Got an error creating stack " + *stackName + " using template from " + *templateFile)
        return
    }

    // snippet-start:[cfn.go.create_stack.wait]
    err = svc.WaitUntilStackCreateComplete(&cloudformation.DescribeStacksInput{
        StackName: stackName,
    })
    if err != nil {
        fmt.Println("Got an error waiting for stack to be created")
        return
    }
    // snippet-end:[cfn.go.create_stack.wait]

    fmt.Println("Created stack " + *stackName + " using template from " + *templateFile)
}
// snippet-end:[cfn.go.create_stack]
