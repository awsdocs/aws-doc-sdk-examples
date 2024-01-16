// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sts.go.assume_role]
package main

import (
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/sts"
)

// Usage:
// go run sts_assume_role.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2"),
	})

	if err != nil {
		fmt.Println("NewSession Error", err)
		return
	}

	// Create a STS client
	svc := sts.New(sess)

	roleToAssumeArn := "arn:aws:iam::123456789012:role/roleName"
	sessionName := "test_session"
	result, err := svc.AssumeRole(&sts.AssumeRoleInput{
		RoleArn:         &roleToAssumeArn,
		RoleSessionName: &sessionName,
	})

	if err != nil {
		fmt.Println("AssumeRole Error", err)
		return
	}

	fmt.Println(result.AssumedRoleUser)
}

// snippet-end:[sts.go.assume_role]
