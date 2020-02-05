/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "flag"
    "fmt"
    "os"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)

func deleteBucketsByPrefix(sess *session.Session, prefix string) error {
    // Create S3 service client
    svc := s3.New(sess)

    // Empty list to hold names of S3 buckets with prefix
    bucketList := make([]string, 0)

    result, err := svc.ListBuckets(nil)
    if err != nil {
        fmt.Println("Could not list buckets")
        os.Exit(1)
    }

    for _, b := range result.Buckets {
        // Does bucket name start with prefix
        if strings.HasPrefix(*b.Name, prefix) {
            bucketList = append(bucketList, *b.Name)
        }
    }

    for _, bucket := range bucketList {
        iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
            Bucket: aws.String(bucket),
        })

        err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
        if err != nil {
            fmt.Println("Unable to delete objects from bucket ", bucket)
            continue
        }

        _, err = svc.DeleteBucket(&s3.DeleteBucketInput{
            Bucket: aws.String(bucket),
        })
        if err != nil {
            fmt.Println("Unable to delete bucket " + bucket)
        }

        err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
            Bucket: aws.String(bucket),
        })
        if err != nil {
            fmt.Println("Error occurred while waiting for bucket to be deleted")
        }

        fmt.Println("Successfully deleted bucket: " + bucket)
    }
    return nil
}

// Deletes any S3 buckets in the default region
// that start with the given text
//
// Usage:
//    go run s3_delete_buckets -p PREFIX [-d}
func main() {
    prefixPtr := flag.String("p", "", "The prefix of the buckets to delete")
    flag.Parse()
    prefix := *prefixPtr

    if prefix == "" {
        fmt.Println("You must supply a bucket prefix")
        os.Exit(1)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    err := deleteBucketsByPrefix(sess, prefix)
    if err != nil {
        fmt.Println("Could not delete buckets with prefix " + prefix)
    } else {
        fmt.Println("Deleted buckets with prefix " + prefix)
    }
}
