// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.AccessKeyLastUsed]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMGetAccessKeyLastUsedAPI defines the interface for the GetAccessKeyLastUsed function.
// We use this interface to test the function using a mocked service.
type IAMGetAccessKeyLastUsedAPI interface {
	GetAccessKeyLastUsed(ctx context.Context,
		params *iam.GetAccessKeyLastUsedInput,
		optFns ...func(*iam.Options)) (*iam.GetAccessKeyLastUsedOutput, error)
}

// WhenWasKeyUsed retrieves when an AWS Identity and Access Management (IAM) access key was last used, including the AWS Region and with which service.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a GetAccessKeyLastUsedOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to GetAccessKeyLastUsed.
func WhenWasKeyUsed(c context.Context, api IAMGetAccessKeyLastUsedAPI, input *iam.GetAccessKeyLastUsedInput) (*iam.GetAccessKeyLastUsedOutput, error) {
	result, err := api.GetAccessKeyLastUsed(c, input)

	return result, err
}

func main() {
	keyID := flag.String("k", "", "The ID of the access key")
	flag.Parse()

	if *keyID == "" {
		fmt.Println("You must supply the ID of an access key (-k KEY-ID)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	input := &iam.GetAccessKeyLastUsedInput{
		AccessKeyId: keyID,
	}

	result, err := WhenWasKeyUsed(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving when access key was last used:")
		fmt.Println(err)
		return
	}

	fmt.Println("The key was last used:", *result.AccessKeyLastUsed)
}

// snippet-end:[iam.go-v2.AccessKeyLastUsed]
