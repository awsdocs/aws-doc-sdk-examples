/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create KMS service client
    svc := kms.New(sess)

    // Encrypt data key
    //
    // Replace the fictitious key ARN with a valid key ID

    keyId := "arn:aws:kms:us-west-2:111122223333:key/1234abcd-12ab-34cd-56ef-1234567890ab"

    text := "1234567890"

    // Encrypt the data key
    result, err := svc.Encrypt(&kms.EncryptInput{
        KeyId: aws.String(keyId),
        Plaintext: []byte(text),
    })

    if err != nil {
        fmt.Println("Got error encrypting data: ", err)
        os.Exit(1)
    }

    fmt.Println("Blob (base-64 byte array):")
    fmt.Println(result.CiphertextBlob)
}
