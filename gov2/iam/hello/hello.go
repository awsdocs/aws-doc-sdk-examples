// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.iam.Hello]

package main

import (
	"context"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/iam"
)

// main uses the AWS SDK for Go (v2) to create an AWS Identity and Access Management (IAM)
// client and list up to 10 policies in your account.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}
	iamClient := iam.NewFromConfig(sdkConfig)
	const maxPols = 10
	fmt.Printf("Let's list up to %v policies for your account.\n", maxPols)
	result, err := iamClient.ListPolicies(context.TODO(), &iam.ListPoliciesInput{
		MaxItems: aws.Int32(maxPols),
	})
	if err != nil {
		fmt.Printf("Couldn't list policies for your account. Here's why: %v\n", err)
		return
	}
	if len(result.Policies) == 0 {
		fmt.Println("You don't have any policies!")
	} else {
		for _, policy := range result.Policies {
			fmt.Printf("\t%v\n", *policy.PolicyName)
		}
	}
}

// snippet-end:[gov2.iam.Hello]
