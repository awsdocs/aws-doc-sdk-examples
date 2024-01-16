// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cognito.go.create_user.complete]
package main

// snippet-start:[cognito.go.create_user.imports]
import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

	"flag"
	"fmt"
	"os"
)

// snippet-end:[cognito.go.create_user.imports]

func main() {
	// snippet-start:[cognito.go.create_user.vars]
	emailIDPtr := flag.String("e", "", "The email address of the user")
	userPoolIDPtr := flag.String("p", "", "The ID of the user pool")
	userNamePtr := flag.String("n", "", "The name of the user")

	flag.Parse()

	if *emailIDPtr == "" || *userPoolIDPtr == "" || *userNamePtr == "" {
		fmt.Println("You must supply an email address, user pool ID, and user name")
		fmt.Println("Usage: go run CreateUser.go -e EMAIL-ADDRESS -p USER-POOL-ID -n USER-NAME")
		os.Exit(1)
	}
	// snippet-end:[cognito.go.create_user.vars]

	// Initialize a session that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	// snippet-start:[cognito.go.create_user.session]
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))
	// snippet-end:[cognito.go.create_user.session]

	// snippet-start:[cognito.go.create_user.create]
	cognitoClient := cognitoidentityprovider.New(sess)

	newUserData := &cognitoidentityprovider.AdminCreateUserInput{
		DesiredDeliveryMediums: []*string{
			aws.String("EMAIL"),
		},
		UserAttributes: []*cognitoidentityprovider.AttributeType{
			{
				Name:  aws.String("email"),
				Value: aws.String(*emailIDPtr),
			},
		},
	}

	newUserData.SetUserPoolId(*userPoolIDPtr)
	newUserData.SetUsername(*userNamePtr)

	_, err := cognitoClient.AdminCreateUser(newUserData)
	if err != nil {
		fmt.Println("Got error creating user:", err)
	}
	// snippet-end:[cognito.go.create_user.create]
}

// snippet-end:[cognito.go.create_user.complete]
