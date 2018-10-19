//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Deletes all of the items in an S3 bucket.]
//snippet-keyword:[Amazon Simple Storage Service]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[s3manager.NewBatchDeleteWithClient function]
//snippet-keyword:[s3manager.NewDeleteListIterator function]
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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
    
    "fmt"
    "os"
)

// Deletes all of the objects in the specified S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_delete_objects BUCKET
func main() {
    if len(os.Args) != 2 {
        exitErrorf("Bucket name required\nUsage: %s BUCKET", os.Args[0])
    }

    bucket := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, _ := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    // Setup BatchDeleteIterator to iterate through a list of objects.
    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: aws.String(bucket),
    })

    // Traverse iterator deleting each object
    if err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter); err != nil {
        exitErrorf("Unable to delete objects from bucket %q, %v", bucket, err)
    }

    fmt.Printf("Deleted object(s) from bucket: %s", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
