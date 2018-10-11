 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Go]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
    "github.com/aws/aws-sdk-go/service/s3"
    "log"
    "strings"
)

// Downloads an item from an S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_download.go
func main() {
    bucket := "myBucket"
    key := "TestFile.txt"

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    _, err = svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: &bucket,
    })
    if err != nil {
        log.Println("Failed to create bucket", err)
        return
    }

    if err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{Bucket: &bucket}); err != nil {
        log.Printf("Failed to wait for bucket to exist %s, %s\n", bucket, err)
        return
    }

    _, err = svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: &bucket,
        Key:    &key,
    })
    if err != nil {
        log.Printf("Failed to upload data to %s/%s, %s\n", bucket, key, err)
        return
    }

    log.Printf("Successfully created bucket %s and uploaded data with key %s\n", bucket, key)
}
