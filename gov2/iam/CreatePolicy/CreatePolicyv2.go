// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.CreatePolicy]
package main

import (
    "context"
    "encoding/json"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMCreatePolicyAPI defines the interface for the CreatePolicy function.
// We use this interface to test the function using a mocked service.
type IAMCreatePolicyAPI interface {
    CreatePolicy(ctx context.Context,
        params *iam.CreatePolicyInput,
        optFns ...func(*iam.Options)) (*iam.CreatePolicyOutput, error)
}

// StatementEntry dictates what this policy allows or doesn't allow.
type StatementEntry struct {
    Effect   string
    Action   []string
    Resource string
}

// PolicyDocument is our definition of our policies to be uploaded to AWS Identity and Access Management (IAM).
type PolicyDocument struct {
    Version   string
    Statement []StatementEntry
}

// CreatePolicyDoc creates a policy document.
func CreatePolicyDoc() ([]byte, error) {
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

    return b, err
}

// MakePolicy creates an IAM policy.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a CreatePolicyOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreatePolicy.
func MakePolicy(c context.Context, api IAMCreatePolicyAPI, input *iam.CreatePolicyInput) (*iam.CreatePolicyOutput, error) {
    result, err := api.CreatePolicy(c, input)

    return result, err
}

func main() {
    policyName := flag.String("n", "", "The name of the policy")
    flag.Parse()

    if *policyName == "" {
        fmt.Println("You must supply the name of the policy (-n POLICY)")
        return
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    b, err := CreatePolicyDoc()
    if err != nil {
        fmt.Println("Got an error creating the policy doc:")
        fmt.Println(err)
        return
    }

    input := &iam.CreatePolicyInput{
        PolicyDocument: aws.String(string(b)),
        PolicyName:     policyName,
    }

    _, err = MakePolicy(context.TODO(), client, input)
    if err != nil {
        fmt.Println("Got an error creating the policy:")
        fmt.Println(err)
        return
    }

    fmt.Println("Created policy " + *policyName)
}

// snippet-end:[iam.go-v2.CreatePolicy]
