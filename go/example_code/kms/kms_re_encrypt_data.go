/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/kms"

	"fmt"
	"os"
)

func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create KMS service client
	svc := kms.New(sess)

	// Encrypt data
	//
	// Replace the fictitious key ARN with a valid key ID

	keyId := "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab"

    // Encrypted data
	blob := []byte{...}

	// Encrypt the data
	result, err := svc.ReEncrypt(&kms.ReEncryptInput{CiphertextBlob: blob, DestinationKeyId: &keyId})

	if err != nil {
		fmt.Println("Got error re-encrypting data: ", err)
		os.Exit(1)
	}

	fmt.Println("Blob (base-64 byte array):")
	fmt.Println(result.CiphertextBlob)
}
