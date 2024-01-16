// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"log"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/glacier"
)

func main() {
	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create Glacier client in default region
	svc := glacier.New(sess)

	// start snippet
	_, err := svc.CreateVault(&glacier.CreateVaultInput{
		VaultName: aws.String("YOUR_VAULT_NAME"),
	})
	if err != nil {
		log.Println(err)
		return
	}

	log.Println("Created vault!")
	// end snippet
}
