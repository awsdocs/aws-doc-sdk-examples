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

// Restores an object in an S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_restore_object.go BUCKET_NAME OBJECT_NAME
func main() {
    if len(os.Args) != 3 {
        exitErrorf("Bucket name and object name required\nUsage: %s bucket_name object_name",
            os.Args[0])
    }

    bucket := os.Args[1]
    obj := os.Args[2]

    // Inititalize a session that the SDK will use to load configuration,
    // credentials, and region from the shared config file. (~/.aws/config).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create S3 service client
    svc := s3.New(sess)

    // Restore the object from Glacier for up to 30 days
    _, err := svc.RestoreObject(&s3.RestoreObjectInput{Bucket: aws.String(bucket), Key: aws.String(obj), RestoreRequest: &s3.RestoreRequest{Days: aws.Int64(30)}})

    if err != nil {
        exitErrorf("Could not restore %s in bucket %s, %v", obj, bucket, err)
    }

    fmt.Printf("%q should be restored to %q in about 4 hours\n", obj, bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
