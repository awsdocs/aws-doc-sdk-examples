// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.DeleteAccountAlias]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMDeleteAccountAliasAPI defines the interface for the DeleteAccountAlias function.
// We use this interface to test the function using a mocked service.
type IAMDeleteAccountAliasAPI interface {
	DeleteAccountAlias(ctx context.Context,
		params *iam.DeleteAccountAliasInput,
		optFns ...func(*iam.Options)) (*iam.DeleteAccountAliasOutput, error)
}

// RemoveAccountAlias deletes an alias for your AWS Identity and Access Management (IAM) account.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a METHODOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to METHOD.
func RemoveAccountAlias(c context.Context, api IAMDeleteAccountAliasAPI, input *iam.DeleteAccountAliasInput) (*iam.DeleteAccountAliasOutput, error) {
	result, err := api.DeleteAccountAlias(c, input)

	return result, err
}

func main() {
	alias := flag.String("a", "", "The account alias")
	flag.Parse()

	if *alias == "" {
		fmt.Println("You must supply an account alias (-a ALIAS)")
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	input := &iam.DeleteAccountAliasInput{
		AccountAlias: alias,
	}

	_, err = RemoveAccountAlias(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error deleting an account alias")
		fmt.Println(err)
		return
	}

	fmt.Printf("Deleted account alias " + *alias)
}

// snippet-end:[iam.go-v2.DeleteAccountAlias]
