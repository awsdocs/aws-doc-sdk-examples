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

	// Encrypted data
	blob := []byte("1234567890")

	// Decrypt the data
	result, err := svc.Decrypt(&kms.DecryptInput{CiphertextBlob: blob})

	if err != nil {
		fmt.Println("Got error decrypting data: ", err)
		os.Exit(1)
	}

	blob_string := string(result.Plaintext)

	fmt.Println(blob_string)
}
