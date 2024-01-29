// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
)

// Deletes a EC2 key pair for the name provided. No error will be returned
// if the key pair does not exist.
//
// Usage:
//
//	go run ec2_delete_keypair.go KEY_PAIR_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("pair name required\nUsage: %s key_pair_name",
			filepath.Base(os.Args[0]))
	}
	pairName := os.Args[1]

	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create an EC2 service client.
	svc := ec2.New(sess)

	// Delete the key pair by name
	_, err = svc.DeleteKeyPair(&ec2.DeleteKeyPairInput{
		KeyName: aws.String(pairName),
	})
	if err != nil {
		if aerr, ok := err.(awserr.Error); ok && aerr.Code() == "InvalidKeyPair.Duplicate" {
			exitErrorf("Key pair %q does not exist.", pairName)
		}
		exitErrorf("Unable to delete key pair: %s, %v.", pairName, err)
	}

	fmt.Printf("Successfully deleted %q key pair\n", pairName)
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(1)
}
