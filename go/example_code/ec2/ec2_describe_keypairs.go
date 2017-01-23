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

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/ec2"
)

// Returns a list of Key Pairs stored in EC2..
//
// Usage:
//    go run ec2_describe_keypairs.go
func main() {
	// Initialize a session that the SDK will use to load configuration,
	// credentials, and region from the shared config file. (~/.aws/config).
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

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
