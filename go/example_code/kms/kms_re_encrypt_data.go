// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/kms"

	"fmt"
	"os"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create KMS service client
	svc := kms.New(sess)

	// Encrypt data key
	//
	// Replace the fictitious key ARN with a valid key ID

	keyId := "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab"

	// Encrypted data
	blob := []byte("1234567890")

	// Re-encrypt the data key
	result, err := svc.ReEncrypt(&kms.ReEncryptInput{CiphertextBlob: blob, DestinationKeyId: &keyId})

	if err != nil {
		fmt.Println("Got error re-encrypting data: ", err)
		os.Exit(1)
	}

	fmt.Println("Blob (base-64 byte array):")
	fmt.Println(result.CiphertextBlob)
}
