// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[cfn.go.list_stacks]
package main

// snippet-start:[cfn.go.list_stacks.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudformation"
)
// snippet-end:[cfn.go.list_stacks.imports]

func statusIsValid(status string) bool {
    switch status {
    case "CREATE_IN_PROGRESS",
        "CREATE_FAILED",
        "CREATE_COMPLETE",
        "ROLLBACK_IN_PROGRESS",
        "ROLLBACK_FAILED",
        "ROLLBACK_COMPLETE",
        "DELETE_IN_PROGRESS",
        "DELETE_FAILED",
        "DELETE_COMPLETE",
        "UPDATE_IN_PROGRESS",
        "UPDATE_COMPLETE_CLEANUP_IN_PROGRESS",
        "UPDATE_COMPLETE",
        "UPDATE_ROLLBACK_IN_PROGRESS",
        "UPDATE_ROLLBACK_FAILED",
        "UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS",
        "UPDATE_ROLLBACK_COMPLETE",
        "REVIEW_IN_PROGRESS",
        "IMPORT_IN_PROGRESS",
        "IMPORT_COMPLETE",
        "IMPORT_ROLLBACK_IN_PROGRESS",
        "IMPORT_ROLLBACK_FAILED",
        "IMPORT_ROLLBACK_COMPLETE":
        return true
    }

    return false
}

// GetStackSummaries gets a list of summary information about all AWS CloudFormation stacks or those with the specified status
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     status is the status of the stack
// Output:
//     If success, the list of stacks and nil
//     Otherwise, nil and an error from the call to ListStacks
func GetStackSummaries(sess *session.Session, status string) (*cloudformation.ListStacksOutput, error) {
    // snippet-start:[cfn.go.list_stacks.call]
    svc := cloudformation.New(sess)
    var filter []*string

    if status != "all" {
        filter = append(filter, aws.String(status))
    }

    input := &cloudformation.ListStacksInput{StackStatusFilter: filter}

    resp, err := svc.ListStacks(input)
    // snippet-end:[cfn.go.list_stacks.call]
    if err != nil {
        return nil, err
    }

    return resp, nil
}

func main() {
    status := flag.String("s", "all", "The status of stacks to display")
    flag.Parse()

    valid := statusIsValid(*status)

    if !valid {
        fmt.Println("Status must be one of:",
            "CREATE_IN_PROGRESS",
            "CREATE_FAILED",
            "CREATE_COMPLETE",
            "ROLLBACK_IN_PROGRESS",
            "ROLLBACK_FAILED",
            "ROLLBACK_COMPLETE",
            "DELETE_IN_PROGRESS",
            "DELETE_FAILED",
            "DELETE_COMPLETE",
            "UPDATE_IN_PROGRESS",
            "UPDATE_COMPLETE_CLEANUP_IN_PROGRESS",
            "UPDATE_COMPLETE",
            "UPDATE_ROLLBACK_IN_PROGRESS",
            "UPDATE_ROLLBACK_FAILED",
            "UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS",
            "UPDATE_ROLLBACK_COMPLETE",
            "REVIEW_IN_PROGRESS",
            "IMPORT_IN_PROGRESS",
            "IMPORT_COMPLETE",
            "IMPORT_ROLLBACK_IN_PROGRESS",
            "IMPORT_ROLLBACK_FAILED",
            "IMPORT_ROLLBACK_COMPLETE")

        return
    }

    // snippet-start:[cfn.go.list_stacks.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cfn.go.list_stacks.session]

    resp, err := GetStackSummaries(sess, *status)
    if err != nil {
        fmt.Println("Could not list stack summary info")
        return
    }

    // snippet-start:[cfn.go.list_stacks.display]
    for _, s := range resp.StackSummaries {
        fmt.Println(*s.StackName + ", Status: " + *s.StackStatus)
    }
    // snippet-end:[cfn.go.list_stacks.display]

    fmt.Println("")
}
// snippet-end:[cfn.go.list_stacks]
