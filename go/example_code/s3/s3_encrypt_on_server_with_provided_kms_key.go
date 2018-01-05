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
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3crypto"

    "fmt"
    "os"
    "strings"
)

func main() {
     if len(os.Args) != 2 {
        fmt.Println("You must supply a bucket name, object name, and key")
        os.Exit(1)
    }

    key := os.Args[0]
    bucket := "myBucket"
    object := "myObject"

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    handler := s3crypto.NewKMSKeyGenerator(kms.New(sess), key)

    // Create an encryption client
    // We need to pass the session here so S3 can use it.
    svc := s3crypto.NewEncryptionClient(sess, s3crypto.AESGCMContentCipherBuilder(handler))

    input := &s3.PutObjectInput{
        Body:   strings.NewReader(object),
        Bucket: aws.String(bucket),
        Key:    aws.String(object),
    }

    _, err := svc.PutObject(input)
    if err != nil {
        fmt.Println("Got an error adding object to bucket")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Added object " + object + " to bucket " + bucket + " with AWS KMS encryption on the client")
}
