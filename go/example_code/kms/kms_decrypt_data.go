//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Decrypts a string that was encrypted by KMS.]
//snippet-keyword:[AWS Key Management Service]
//snippet-keyword:[Decrypt function]
//snippet-keyword:[Go]
//snippet-service:[kms]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
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

    // Encrypted data
    blob := []byte{...}

    // Decrypt the data
    result, err := svc.Decrypt(&kms.DecryptInput{CiphertextBlob: blob})

    if err != nil {
        fmt.Println("Got error decrypting data: ", err)
        os.Exit(1)
    }

    blob_string := string(result.Plaintext)

    fmt.Println(blob_string)
}
