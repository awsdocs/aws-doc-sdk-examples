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
        exitErrorf("Bucket name required\nUsage: %s BUCKET",
            os.Args[0])
    }

    bucket := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, _ := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    // Get the list of objects
    // Note that if the bucket has more than 1000 objects,
    // we have to run this multiple times
    hasMoreObjects := true
    // Keep track of how many objects we delete
    totalObjects := 0

    for hasMoreObjects {
        resp, err := svc.ListObjects(&s3.ListObjectsInput{Bucket: aws.String(bucket)})
        if err != nil {
            exitErrorf("Unable to list items in bucket %q, %v", bucket, err)
        }

        numObjs := len(resp.Contents)
        totalObjects += numObjs

        // Create Delete object with slots for the objects to delete
        var items s3.Delete
        var objs= make([]*s3.ObjectIdentifier, numObjs)

        for i, o := range resp.Contents {
            // Add objects from command line to array
            objs[i] = &s3.ObjectIdentifier{Key: aws.String(*o.Key)}
        }

        // Add list of objects to delete to Delete object
        items.SetObjects(objs)

        // Delete the items
        _, err = svc.DeleteObjects(&s3.DeleteObjectsInput{Bucket: &bucket, Delete: &items})
        if err != nil {
            exitErrorf("Unable to delete objects from bucket %q, %v", bucket, err)
        }

        hasMoreObjects = *resp.IsTruncated
    }

    fmt.Println("Deleted", totalObjects, "object(s) from bucket", bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
