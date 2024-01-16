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
// go run iam_getpublickeys.go <username>
func main() {
	// Initialize a session in us-west-2 that the SDK will use to load
	// credentials from the shared credentials file ~/.aws/credentials.
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-west-2")},
	)

	// Create a IAM service client.
	svc := iam.New(sess)

	// List SSH public keys.
	keysResult, err := svc.ListSSHPublicKeys(&iam.ListSSHPublicKeysInput{
		UserName: &os.Args[1],
	})

	if err != nil {
		fmt.Println("Error", err)
		return
	}

	for _, key := range keysResult.SSHPublicKeys {
		if key == nil {
			continue
		}

		// Get a SSH public key.
		keyResult, err := svc.GetSSHPublicKey(&iam.GetSSHPublicKeyInput{
			UserName:       &os.Args[1],
			SSHPublicKeyId: key.SSHPublicKeyId,
			Encoding:       aws.String("SSH"),
		})

		if err != nil {
			continue
		}

		fmt.Printf("%s\n", *keyResult.SSHPublicKey.SSHPublicKeyBody)
	}
}
