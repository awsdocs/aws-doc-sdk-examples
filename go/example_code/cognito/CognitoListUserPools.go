// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cognito.go.list_user_pools]
package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

	"fmt"
	"os"
)

func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create Cognito client
	svc := cognitoidentityprovider.New(sess)

	max := int64(10)

	result, err := svc.ListUserPools(
		&cognitoidentityprovider.ListUserPoolsInput{
			MaxResults: &max,
		}) // .ListBuckets(nil)
	if err != nil {
		fmt.Println("Could not list user pools")
		os.Exit(1)
	}

	fmt.Println("User pools:")
	fmt.Println("")

	for _, pool := range result.UserPools {
		fmt.Println("Name: " + aws.StringValue(pool.Name))
		fmt.Println("ID:   " + aws.StringValue(pool.Id))
		fmt.Println("Created: " + aws.TimeValue(pool.CreationDate).String())
		fmt.Println("")
	}
}

// snippet-end:[cognito.go.list_user_pools]
