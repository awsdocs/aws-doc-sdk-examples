// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[iam.go-v2.ListUsers]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go/aws"
)

// IAMListUsersAPI defines the interface for the ListUsers function.
// We use this interface to test the function using a mocked service.
type IAMListUsersAPI interface {
	ListUsers(ctx context.Context,
		params *iam.ListUsersInput,
		optFns ...func(*iam.Options)) (*iam.ListUsersOutput, error)
}

// GetUsers retrieves a list of your AWS Identity and Access Management (IAM) users.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a ListUsersOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to ListUsers.
func GetUsers(c context.Context, api IAMListUsersAPI, input *iam.ListUsersInput) (*iam.ListUsersOutput, error) {
	return api.ListUsers(c, input)
}

func main() {
	maxUsers := flag.Int("m", 10, "The maximum number of users to return")
	flag.Parse()

	if *maxUsers < 0 {
		*maxUsers = 10
	}

	if *maxUsers > 100 {
		*maxUsers = 1000
	}

	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := iam.NewFromConfig(cfg)

	input := &iam.ListUsersInput{
		MaxItems: aws.Int32(int32((*maxUsers))),
	}

	result, err := GetUsers(context.Background(), client, input)
	if err != nil {
		fmt.Println("Got an error retrieving users:")
		fmt.Println(err)
		return
	}

	for _, user := range result.Users {
		fmt.Println(*user.UserName+" created on", *user.CreateDate)
	}
}

// snippet-end:[iam.go-v2.ListUsers]
