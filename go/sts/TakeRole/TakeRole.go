// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[sts.go.take_role]
package main

// snippet-start:[sts.go.take_role.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sts"
    "github.com/aws/aws-sdk-go/service/sts/stsiface"
)
// snippet-end:[sts.go.take_role.imports]

// TakeRole gets temporary security credentials to access resources
// Inputs:
//     svc is an AWS STS service client
//     roleARN is the Amazon Resource Name (ARN) of the role to assume
//     sessionName is a unique identifier for the session
// Output:
//     If success, information about the assumed role and nil
//     Otherwise, nil and an error from the call to AssumeRole
func TakeRole(svc stsiface.STSAPI, roleARN, sessionName *string) (*sts.AssumeRoleOutput, error) {
    // snippet-start:[sts.go.take_role.call]
    result, err := svc.AssumeRole(&sts.AssumeRoleInput{
        RoleArn:         roleARN,
        RoleSessionName: sessionName,
    })
    // snippet-end:[sts.go.take_role.call]

    return result, err
}

func main() {
    // snippet-start:[sts.go.take_role.args]
    roleARN := flag.String("r", "", "The Amazon Resource Name (ARN) of the role to assume")
    sessionName := flag.String("s", "", "A unique identifier for the session")

    if *roleARN == "" || *sessionName == "" {
        fmt.Println("You must supply a role ARN and session name")
        fmt.Println("-r ROLE-ARN -S SESSION-NAME")
        return
    }
    // snippet-end:[sts.go.take_role.args]

    // snippet-start:[sts.go.take_role.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sts.New(sess)
    // snippet-end:[sts.go.take_role.session]

    result, err := TakeRole(svc, roleARN, sessionName)
    if err != nil {
        fmt.Println("Got an error assuming the role:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sts.go.take_role.display]
    fmt.Println(result.AssumedRoleUser)
    // credentials := *result.Credentials
    // snippet-end:[sts.go.take_role.display]
}
// snippet-end:[sts.go.take_role]
