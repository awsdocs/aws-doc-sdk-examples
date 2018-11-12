//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Deletes a policy for an S3 bucket.]
//snippet-keyword:[Amazon Simple Storage Service]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[DeleteBucketPolicy function]
//snippet-keyword:[Go]
//snippet-service:[s3]
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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "fmt"
    "os"
    "path/filepath"
)

// Deletes the policy on a bucket. If the bucket doesn't exist, or there was
// and error an error message will be printed instead.
//
// Usage:
//    go run s3_delete_bucket_policy.go BUCKET_NAME
func main() {
    if len(os.Args) != 2 {
        exitErrorf("bucket name required\nUsage: %s bucket_name",
            filepath.Base(os.Args[0]))
    }
    bucket := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    // Call S3 to delete the policy on the bucket.
    _, err = svc.DeleteBucketPolicy(&s3.DeleteBucketPolicyInput{
        Bucket: aws.String(bucket),
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok && aerr.Code() == s3.ErrCodeNoSuchBucket {
            // Special error handling for the when the bucket doesn't
            // exists so we can give a more direct error message from the CLI.
            exitErrorf("Bucket %q does not exist", bucket)
        }
        exitErrorf("Unable to delete bucket %q policy, %v", bucket, err)
    }

    fmt.Printf("Successfully deleted the policy on bucket %q.\n", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
