// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_createaccountalias.go <alias>
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create a IAM service client.
	svc := iam.New(sess)

	_, err = svc.CreateAccountAlias(&iam.CreateAccountAliasInput{
		AccountAlias: &os.Args[1],
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Printf("Account alias %s has been created\n", os.Args[1])
}
