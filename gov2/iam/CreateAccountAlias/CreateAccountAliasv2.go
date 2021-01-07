// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.CreateAccountAlias]
package main

import (
    "context"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMCreateAccountAliasAPI defines the interface for the CreateAccountAlias function.
// We use this interface to test the function using a mocked service.
type IAMCreateAccountAliasAPI interface {
    CreateAccountAlias(ctx context.Context,
        params *iam.CreateAccountAliasInput,
        optFns ...func(*iam.Options)) (*iam.CreateAccountAliasOutput, error)
}

// MakeAccountAlias creates an alias for your AWS Identity and Access Management (IAM) account.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a CreateAccountAliasOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateAccountAlias.
func MakeAccountAlias(c context.Context, api IAMCreateAccountAliasAPI, input *iam.CreateAccountAliasInput) (*iam.CreateAccountAliasOutput, error) {
    results, err := api.CreateAccountAlias(c, input)

    return results, err
}

func main() {
    alias := flag.String("a", "", "The account alias")
    flag.Parse()

    if *alias == "" {
        fmt.Println("You must supply an account alias (-a ALIAS)")
    }

    cfg, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        panic("configuration error, " + err.Error())
    }

    client := iam.NewFromConfig(cfg)

    input := &iam.CreateAccountAliasInput{
        AccountAlias: alias,
    }

    _, err = MakeAccountAlias(context.Background(), client, input)
    if err != nil {
        fmt.Println("Got an error creating an account alias")
        fmt.Println(err)
        return
    }

    fmt.Printf("Created account alias " + *alias)
}

// snippet-end:[iam.go-v2.CreateAccountAlias]
