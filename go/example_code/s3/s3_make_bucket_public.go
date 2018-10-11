 
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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"

    "fmt"
    "os"
)

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}

// Gives everyone read-only access to BUCKET.
//
// Usage:
//    go run s3_make_bucket_public.go BUCKET
func main() {
    if len(os.Args) < 2 {
        exitErrorf("Bucket name required.\nUsage: go run", os.Args[0], "BUCKET")
    }

    bucket := os.Args[1]

    // private | public-read | public-read-write | authenticated-read
    // See https://docs.aws.amazon.com/AmazonS3/latest/dev/acl-overview.html#CannedACL for details
    acl := "public-read"

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and region from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create S3 service client
    svc := s3.New(sess)

    params := &s3.PutBucketAclInput{
        Bucket: &bucket,
        ACL: &acl,
    }

    // Set bucket ACL
    _, err := svc.PutBucketAcl(params)
    if err != nil {
        exitErrorf(err.Error())
    }

    fmt.Println("Bucket " + bucket + " is now public")
}
