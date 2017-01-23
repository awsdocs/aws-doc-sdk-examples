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
//    go run ec2_delete_keypair.go KEY_PAIR_NAME
func main() {
	if len(os.Args) != 2 {
		exitErrorf("pair name required\nUsage: %s key_pair_name",
			filepath.Base(os.Args[0]))
	}
	pairName := os.Args[1]

	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create an EC2 service client.
	svc := ec2.New(sess)

	// Delete the key pair by name
	_, err := svc.DeleteKeyPair(&ec2.DeleteKeyPairInput{
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
