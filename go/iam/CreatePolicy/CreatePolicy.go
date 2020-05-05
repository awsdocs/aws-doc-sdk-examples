// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[iam.go.create_policy]
package main

// snippet-start:[iam.go.create_policy.imports]
import (
    "encoding/json"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/iam/iamiface"
)
// snippet-end:[iam.go.create_policy.imports]

// StatementEntry will dictate what this policy will allow or not allow.
// snippet-start:[iam.go.create_policy.structs]
type StatementEntry struct {
    Effect   string
    Action   []string
    Resource string
}

// PolicyDocument is our definition of our policies to be uploaded to IAM.
type PolicyDocument struct {
    Version   string
    Statement []StatementEntry
}
// snippet-end:[iam.go.create_policy.structs]

// MakePolicy creates an IAM policy
// Inputs:
//     svc is an IAM service client
// Output:
//     If success, nil
//     Otherwise, an error from the call to json.Marshall or CreatePolicy
func MakePolicy(svc iamiface.IAMAPI, policyName *string) error {
    // snippet-start:[iam.go.create_policy.doc]
    // Builds our policy document for IAM.
    policy := PolicyDocument{
        Version: "2012-10-17",
        Statement: []StatementEntry{
            StatementEntry{
                Effect: "Allow",
                Action: []string{
                    "logs:CreateLogGroup", // Allow for creating log groups
                },
                Resource: "RESOURCE ARN FOR logs:*",
            },
            StatementEntry{
                Effect: "Allow",
                // Allows for DeleteItem, GetItem, PutItem, Scan, and UpdateItem
                Action: []string{
                    "dynamodb:DeleteItem",
                    "dynamodb:GetItem",
                    "dynamodb:PutItem",
                    "dynamodb:Scan",
                    "dynamodb:UpdateItem",
                },
                Resource: "RESOURCE ARN FOR dynamodb:*",
            },
        },
    }

    b, err := json.Marshal(&policy)
    // snippet-end:[iam.go.create_policy.doc]
    if err != nil {
        return err
    }

    // snippet-start:[iam.go.create_policy.call]
    _, err = svc.CreatePolicy(&iam.CreatePolicyInput{
        PolicyDocument: aws.String(string(b)),
        PolicyName:     policyName,
    })
    // snippet-end:[iam.go.create_policy.call]
    return err
}

func main() {
    // snippet-start:[iam.go.create_policy.args]
    policyName := flag.String("n", "", "The name of the policy")
    flag.Parse()

    if *policyName == "" {
        fmt.Println("You must supply the name of the policy (-n POLICY)")
        return
    }
    // snippet-end:[iam.go.create_policy.args]

    // snippet-start:[iam.go.create_policy.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := iam.New(sess)
    // snippet-end:[iam.go.create_policy.session]

    err := MakePolicy(svc, policyName)
    if err != nil {
        fmt.Println("Got an error creating the policy:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created policy " + *policyName)
}
// snippet-end:[iam.go.create_policy]
