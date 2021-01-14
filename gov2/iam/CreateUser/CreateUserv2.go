// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.CreateUser]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// IAMCreateUserAPI defines the interface for the CreateUser function.
// We use this interface to test the function using a mocked service.
type IAMCreateUserAPI interface {
	CreateUser(ctx context.Context,
		params *iam.CreateUserInput,
		optFns ...func(*iam.Options)) (*iam.CreateUserOutput, error)
}

// MakeUser creates an AWS Identity and Access Management (IAM) user.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a CreateUserOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to CreateUser.
func MakeUser(c context.Context, api IAMCreateUserAPI, input *iam.CreateUserInput) (*iam.CreateUserOutput, error) {
	return api.CreateUser(c, input)
}

func main() {
	userName := flag.String("u", "", "The name of the user to create.")
	flag.Parse()

	if *userName == "" {
		fmt.Println("You must supply a user name (-u USERNAME)")
		return
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	input := &iam.CreateUserInput{
		UserName: userName,
	}

	results, err := MakeUser(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error creating user " + *userName)
	}

	fmt.Println("Created user " + *results.User.UserName)
}

// snippet-end:[iam.go-v2.CreateUser]
