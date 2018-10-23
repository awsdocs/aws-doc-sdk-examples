//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Sets the policy for an S3 bucket.]
//snippet-keyword:[Amazon Simple Storage Service]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[PutBucketPolicy function]
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
    "encoding/json"
    "fmt"
    "os"
    "path/filepath"
)

// Sets a read only anonymous user policy for a bucket. If the bucket doesn't
// exist, or there was and error an error message will be printed instead.
//
// Usage:
//    go run s3_put_bucket_policy.go BUCKET_NAME
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

    // Create a policy using map interface. Filling in the bucket as the
    // resource.
    readOnlyAnonUserPolicy := map[string]interface{}{
        "Version": "2012-10-17",
        "Statement": []map[string]interface{}{
            {
                "Sid":       "AddPerm",
                "Effect":    "Allow",
                "Principal": "*",
                "Action": []string{
                    "s3:GetObject",
                },
                "Resource": []string{
                    fmt.Sprintf("arn:aws:s3:::%s/*", bucket),
                },
            },
        },
    }

    // Marshal the policy into a JSON value so that it can be sent to S3.
    policy, err := json.Marshal(readOnlyAnonUserPolicy)
    if err != nil {
        exitErrorf("Failed to marshal policy, %v", err)
    }

    // Call S3 to put the policy for the bucket.
    _, err = svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
        Bucket: aws.String(bucket),
        Policy: aws.String(string(policy)),
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok && aerr.Code() == s3.ErrCodeNoSuchBucket {
            // Special error handling for the when the bucket doesn't
            // exists so we can give a more direct error message from the CLI.
            exitErrorf("Bucket %q does not exist", bucket)
        }
        exitErrorf("Unable to set bucket %q policy, %v", bucket, err)
    }

    fmt.Printf("Successfully set bucket %q's policy\n", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
