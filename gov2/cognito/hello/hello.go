// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.cognito-identity-provider.hello]

package main

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider"
	"github.com/aws/aws-sdk-go-v2/service/cognitoidentityprovider/types"
)

// main uses the AWS SDK for Go V2 to create an Amazon Simple Notification Service
// (Amazon SNS) client and list the topics in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	cognitoClient := cognitoidentityprovider.NewFromConfig(sdkConfig)
	fmt.Println("Let's list the user pools for your account.")
	var pools []types.UserPoolDescriptionType
	paginator := cognitoidentityprovider.NewListUserPoolsPaginator(
		cognitoClient, &cognitoidentityprovider.ListUserPoolsInput{MaxResults: aws.Int32(10)})
	for paginator.HasMorePages() {
		output, err := paginator.NextPage(ctx)
		if err != nil {
			log.Printf("Couldn't get user pools. Here's why: %v\n", err)
		} else {
			pools = append(pools, output.UserPools...)
		}
	}
	if len(pools) == 0 {
		fmt.Println("You don't have any user pools!")
	} else {
		for _, pool := range pools {
			fmt.Printf("\t%v: %v\n", *pool.Name, *pool.Id)
		}
	}
}

// snippet-end:[gov2.cognito-identity-provider.hello]
