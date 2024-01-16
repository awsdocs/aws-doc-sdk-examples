// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/iam"
)

// Usage:
// go run iam_deleteuser.go <username>
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create a IAM service client.
	svc := iam.New(sess)

	_, err = svc.DeleteUser(&iam.DeleteUserInput{
		UserName: &os.Args[1],
	})

	// If the user does not exist than we will log an error.
	if awserr, ok := err.(awserr.Error); ok && awserr.Code() == iam.ErrCodeNoSuchEntityException {
		fmt.Printf("User %s does not exist\n", os.Args[1])
		return
	} else if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Printf("User %s has been deleted\n", os.Args[1])
}
