// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.ListAccountAliases]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
    "github.com/aws/aws-sdk-go/aws"
)

// IAMListAccountAliasesAPI defines the interface for the ListAccountAliases function.
// We use this interface to test the function using a mocked service.
type IAMListAccountAliasesAPI interface {
    ListAccountAliases(ctx context.Context,
        params *iam.ListAccountAliasesInput,
        optFns ...func(*iam.Options)) (*iam.ListAccountAliasesOutput, error)
}

// GetAccountAliases retrieves the aliases for your IAM account.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a ListAccountAliasesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListAccountAliases.
func GetAccountAliases(c context.Context, api IAMListAccountAliasesAPI, input *iam.ListAccountAliasesInput) (*iam.ListAccountAliasesOutput, error) {
    result, err := api.ListAccountAliases(c, input)

    return result, err
}

func main() {
    maxItems := flag.Int("m", 10, "Maximum number of aliases to list")
    flag.Parse()

    if *maxItems < 0 {
        *maxItems = 10
    }

    cfg, err := config.LoadDefaultConfig()
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    input := &iam.ListAccountAliasesInput{
        MaxItems: aws.Int32(int32(*maxItems)),
    }

    result, err := GetAccountAliases(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error retrieving account aliases")
        fmt.Println(err)
        return
    }

    for i, alias := range result.AccountAliases {
        fmt.Printf("Alias %d: %s\n", i, *alias)
    }
}

// snippet-end:[iam.go-v2.ListAccountAliases]
