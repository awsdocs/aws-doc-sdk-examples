// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Gets an IAM public key.]
// snippet-keyword:[AWS Identity and Access Management]
// snippet-keyword:[GetSSHPublicKey function]
// snippet-keyword:[ListSSHPublicKeys function]
// snippet-keyword:[Go]
// snippet-service:[iam]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
