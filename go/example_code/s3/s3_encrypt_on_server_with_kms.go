// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Adds an item to an S3 bucket with server-side encryption set to AWS KMS.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[PutObject function]
// snippet-keyword:[Go]
// snippet-service:[s3]
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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "fmt"
    "os"
    "strings"
)

func main() {
    if len(os.Args) != 2 {
        fmt.Println("You must supply a key")
        os.Exit(1)
    }

    key := os.Args[1]
    bucket := "myBucket"
    object := "myItem"

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := s3.New(sess)

    input := &s3.PutObjectInput{
        Body:                 strings.NewReader(object),
        Bucket:               aws.String(bucket),
        Key:                  aws.String(object),
        ServerSideEncryption: aws.String("aws:kms"),
        SSEKMSKeyId:          aws.String(key),
    }

    _, err := svc.PutObject(input)
    if err != nil {
        fmt.Println("Got an error adding object to bucket")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Added object " + obj + " to bucket " + bucket + " with AWS KMS encryption")
}
