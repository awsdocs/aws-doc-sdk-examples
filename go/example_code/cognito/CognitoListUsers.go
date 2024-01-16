// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cognito.go.list_users.complete]
package main

// snippet-start:[cognito.go.list_users.imports]
import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

	"flag"
	"fmt"
	"os"
)

// snippet-end:[cognito.go.list_users.imports]

func main() {
	// snippet-start:[cognito.go.list_users.vars]
	userPoolIDPtr := flag.String("p", "", "The ID of the user pool")

	flag.Parse()

	if *userPoolIDPtr == "" {
		fmt.Println("You must supply a user pool ID")
		fmt.Println("Usage: go run CreateUser.go -p USER-POOL-ID")
		os.Exit(1)
	}
	// snippet-end:[cognito.go.list_users.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	// snippet-start:[cognito.go.list_users.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[cognito.go.list_users.session]

	// snippet-start:[cognito.go.list_users.list]
	cognitoClient := cognitoidentityprovider.New(sess)

	results, err := cognitoClient.ListUsers(
		&cognitoidentityprovider.ListUsersInput{
			UserPoolId: userPoolIDPtr})
	if err != nil {
		fmt.Println("Got error listing users")
		os.Exit(1)
	}

	// Show their names an email addresses
	for _, user := range results.Users {
		attributes := user.Attributes

		for _, a := range attributes {
			if *a.Name == "name" {
				fmt.Println("Name:  " + *a.Value)
			} else if *a.Name == "email" {
				fmt.Println("Email: " + *a.Value)
			}
		}

		fmt.Println("")
	}
	// snippet-end:[cognito.go.list_users.list]
}

// snippet-end:[cognito.go.list_users.complete]
