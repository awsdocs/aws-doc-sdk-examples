// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Decrypts a string that was encrypted by KMS.]
// snippet-keyword:[AWS Key Management Service]
// snippet-keyword:[Decrypt function]
// snippet-keyword:[Go]
// snippet-service:[kms]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-03-14]
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
