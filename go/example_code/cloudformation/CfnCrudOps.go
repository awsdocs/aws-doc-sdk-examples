/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "flag"
    "fmt"
    "io/ioutil"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudformation"
)

// CreateStack creates a CloudFormation stack
func CreateStack(sess *session.Session, stackName string, template string) error {
    svc := cloudformation.New(sess)
    // Open file template
    // Get entire file as a string
    content, err := ioutil.ReadFile(template)
    if err != nil {
        return err
    }

    // Convert []byte to string
    templateBody := string(content)

    input := &cloudformation.CreateStackInput{TemplateBody: aws.String(templateBody), StackName: aws.String(stackName)}

    _, err = svc.CreateStack(input)
    if err != nil {
        return err
    }

    // Wait until stack is created
    desInput := &cloudformation.DescribeStacksInput{StackName: aws.String(stackName)}
    return svc.WaitUntilStackCreateComplete(desInput)
}

// GetStackSummaries gets a list of summary information about all stacks or those with status
func GetStackSummaries(sess *session.Session, status string) ([]*cloudformation.StackSummary, error) {
    svc := cloudformation.New(sess)
    var stackSummaries []*cloudformation.StackSummary

    var filter []*string

    if status != "all" {
        filter = append(filter, aws.String(status))
    }

    input := &cloudformation.ListStacksInput{StackStatusFilter: filter}

    resp, err := svc.ListStacks(input)
    if err != nil {
        return stackSummaries, err
    }

    return resp.StackSummaries, nil
}

// DeleteStack deletes the specified stack
func DeleteStack(sess *session.Session, name string) error {
    svc := cloudformation.New(sess)
    delInput := &cloudformation.DeleteStackInput{StackName: aws.String(name)}
    _, err := svc.DeleteStack(delInput)
    if err != nil {
        return err
    }

    // Wait until stack is created
    desInput := &cloudformation.DescribeStacksInput{StackName: aws.String(name)}
    return svc.WaitUntilStackDeleteComplete(desInput)
}

func main() {
    operationPtr := flag.String("o", "", "The operation to perform: create, list, delete, or all in that order")
    stackNamePtr := flag.String("n", "", "The name of the stack to create or delete")
    templateFilePtr := flag.String("t", "", "The name of the file containing the CloudFormation template")
    flag.Parse()
    operation := *operationPtr
    stackName := *stackNamePtr
    templateFile := *templateFilePtr

    if (operation == "create" || operation == "delete" || operation == "all") && stackName == "" {
        // Create dummy name using guid
        // Create a unique GUID for stack name
        id := uuid.New()
        stackName = "stack-" + id.String()
    }

    if (operation == "create" || operation == "all") && templateFile == "" {
        fmt.Println("You must supply the name of the template to use to create a stack")
        return
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    switch operation {
    case "all":
        // Create stack
        err := CreateStack(sess, stackName, templateFile)
        if err != nil {
            fmt.Println("Could not create stack " + stackName)
        }

        // Get all stacks
        summaries, err := GetStackSummaries(sess, "all")
        if err != nil {
            fmt.Println("Could not list stack summary info")
            return
        }

        for _, s := range summaries {
            fmt.Println(*s.StackName + ", Status: " + *s.StackStatus)
        }

        fmt.Println("")

        // Delete stack
        err = DeleteStack(sess, stackName)
        if err != nil {
            fmt.Println("Could not delete stack " + stackName)
        }
    case "create":
        err := CreateStack(sess, stackName, templateFile)
        if err != nil {
            fmt.Println("Could not create stack " + stackName)
        }
    case "list":
        summaries, err := GetStackSummaries(sess, "all")
        if err != nil {
            fmt.Println("Could not list stack summary info")
            return
        }

        for _, s := range summaries {
            fmt.Println(*s.StackName + ", Status: " + *s.StackStatus)
        }

        fmt.Println("")
    case "delete":
        err := DeleteStack(sess, stackName)
        if err != nil {
            fmt.Println("Could not delete stack " + stackName)
        }
    default:
        fmt.Println("Unrecognized operation: " + operation)
    }
}
