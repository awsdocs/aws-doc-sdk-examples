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

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)

// Creates an S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_create_bucket BUCKET_NAME
func main() {
    if len(os.Args) != 2 {
        exitErrorf("Bucket name missing!\nUsage: %s bucket_name", os.Args[0])
    }

    bucket := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    // Create the S3 Bucket
    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: aws.String(bucket),
    })

    if err != nil {
        exitErrorf("Unable to create bucket %q, %v", bucket, err)
    }

    // Wait until bucket is created before finishing
    fmt.Printf("Waiting for bucket %q to be created...\n", bucket)

    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: aws.String(bucket),
    })

    if err != nil {
        exitErrorf("Error occurred while waiting for bucket to be created, %v", bucket)
    }

    fmt.Printf("Bucket %q successfully created\n", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
