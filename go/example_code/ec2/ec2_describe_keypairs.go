// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"

	"fmt"
	"os"
)

// Returns a list of Key Pairs stored in EC2..
//
// Usage:
//
//	go run ec2_describe_keypairs.go
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create an EC2 service client.
	svc := ec2.New(sess)

	//  Returns a list of key pairs
	result, err := svc.DescribeKeyPairs(nil)
	if err != nil {
		exitErrorf("Unable to get key pairs, %v", err)
	}

	fmt.Println("Key Pairs:")
	for _, pair := range result.KeyPairs {
		fmt.Printf("%s: %s\n", *pair.KeyName, *pair.KeyFingerprint)
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
