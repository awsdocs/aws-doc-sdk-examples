// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Gets the ACLs for an S3 bucket item.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[GetObjectAcl function]
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
)

// Gets the ACL for a bucket object
//
// Usage:
//     go run s3_get_bucket_object_acl.go BUCKET OBJECT
func main() {
    if len(os.Args) != 3 {
        exitErrorf("Bucket and object names required\nUsage: go run", os.Args[0], "BUCKET OBJECT")
    }

    bucket := os.Args[1]
    key := os.Args[2]

    // Initialize a session that loads credentials from the shared credentials file ~/.aws/credentials
    // and the region from the shared configuratin file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create S3 service client
    svc := s3.New(sess)

    // Get bucket ACL
    result, err := svc.GetObjectAcl(&s3.GetObjectAclInput{Bucket: &bucket, Key: &key})
    if err != nil {
        exitErrorf(err.Error())
    }

    fmt.Println("Owner:", *result.Owner.DisplayName)
    fmt.Println("")
    fmt.Println("Grants")

    for _, g := range result.Grants {
        fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
        fmt.Println("  Type:      ", *g.Grantee.Type)
        fmt.Println("  Permission:", *g.Permission)
        fmt.Println("")
    }
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
