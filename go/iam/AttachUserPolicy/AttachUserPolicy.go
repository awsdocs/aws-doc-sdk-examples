// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.attach_user_policy]
package main

// snippet-start:[iam.go.attach_user_policy.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.attach_user_policy.imports]

// AttachDynamoFullPolicy attaches a DynamoDB full-access policy to an IAM role
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     roleName is the name of the IAM role
// Output:
//     If success, nil
//     Otherwise, an error from the call to AttachRolePolicy
func AttachDynamoFullPolicy(svc iamiface.IAMAPI, roleName *string) error {
    // snippet-start:[iam.go.attach_user_policy.call]
    policyArn := "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

    _, err := svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
        PolicyArn: &policyArn,
        RoleName:  roleName,
    })
    // snippet-end:[iam.go.attach_user_policy.call]
    return err
}

func main() {
    // snippet-start:[iam.go.attach_user_policy.args]
    roleName := flag.String("r", "", "The name of the IAM role")
    flag.Parse()

    if *roleName == "" {
        fmt.Println("You must supply a role name (-r ROLE)")
        return
    }
    // snippet-end:[iam.go.attach_user_policy.args]

    // snippet-start:[iam.go.attach_user_policy.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.attach_user_policy.session]

    err := AttachDynamoFullPolicy(svc, roleName)
    if err != nil {
        fmt.Println("Unable to attach DynamoDB full-access role policy to role")
        return
    }
    fmt.Println("Role attached successfully")
}
// snippet-end:[iam.go.attach_user_policy]
