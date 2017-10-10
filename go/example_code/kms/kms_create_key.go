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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/kms"

    "fmt"
    "os"
)

// Create a customer master key (CMK)
// Since we are only encrypting small amounts of data (4 KiB or less) directly,
// a CMK is fine for our purposes.
// For larger amounts of data,
// use the CMK to encrypt a data encryption key (DEK).

func main() {
    // Initialize a session that the SDK will use to load configuration,
    // credentials, and region from the shared config file. (~/.aws/config).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create KMS service client
    svc := kms.New(sess)

    // Create the key
    result, err := svc.CreateKey(&kms.CreateKeyInput{
        Tags: []*kms.Tag{
            {
                TagKey:   aws.String("CreatedBy"),
                TagValue: aws.String("ExampleUser"),
            },
        },
    })

    if err != nil {
        fmt.Println("Got error creating key: ", err)
        os.Exit(1)
    }

    fmt.Println("ARN: " + *result.KeyMetadata.Arn)
}
