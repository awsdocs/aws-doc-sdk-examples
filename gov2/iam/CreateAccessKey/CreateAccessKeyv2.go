// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.CreateAccessKey]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// IMACreateAccessKeyAPI defines the interface for the CreateAccessKey function.
// We use this interface to test the function using a mocked service.
type IAMCreateAccessKeyAPI interface {
	CreateAccessKey(ctx context.Context,
		params *iam.CreateAccessKeyInput,
		optFns ...func(*iam.Options)) (*iam.CreateAccessKeyOutput, error)
}

// MakeAccessKey creates a new AWS Identity and Access Management (IAM) access key for a user.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a CreateAccessKeyOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateAccessKey.
func MakeAccessKey(c context.Context, api IAMCreateAccessKeyAPI, input *iam.CreateAccessKeyInput) (*iam.CreateAccessKeyOutput, error) {
	return api.CreateAccessKey(c, input)
}

func main() {
	userName := flag.String("u", "", "The name of the user")
	flag.Parse()

	if *userName == "" {
		fmt.Println("You must supply a user name (-u USER)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	input := &iam.CreateAccessKeyInput{
		UserName: userName,
	}

	result, err := MakeAccessKey(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error creating a new access key")
		fmt.Println(err)
		return
	}

	fmt.Println("Created new access key with ID: " + *result.AccessKey.AccessKeyId + " and secret key: " + *result.AccessKey.SecretAccessKey)
}

// snippet-end:[iam.go-v2.CreateAccessKey]
