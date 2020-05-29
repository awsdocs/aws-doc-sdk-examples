/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[s3.go.get_bucket_acl]
package main

// snippet-start:[s3.go.get_bucket_acl.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.get_bucket_acl.imports]

// GetBucketACL gets the ACL for a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to GetBucketAcl
func GetBucketACL(sess *session.Session, bucket *string) (*s3.GetBucketAclOutput, error) {
    // snippet-start:[s3.go.get_bucket_acl.call]
    svc := s3.New(sess)

    result, err := svc.GetBucketAcl(&s3.GetBucketAclInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.get_bucket_acl.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[s3.go.get_bucket_acl.args]
    bucket := flag.String("b", "", "The bucket for which the ACL is returned")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.get_bucket_acl.args]

    // snippet-start:[s3.go.get_bucket_acl.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.get_bucket_acl.session]

    result, err := GetBucketACL(sess, bucket)
    if err != nil {
        fmt.Println("Got an error retrieving ACL for " + *bucket)
    }

    // snippet-start:[s3.go.get_bucket_acl.print]
    fmt.Println("Owner:", *result.Owner.DisplayName)
    fmt.Println("")
    fmt.Println("Grants")

    for _, g := range result.Grants {
        // If we add a canned ACL, the name is nil
        if g.Grantee.DisplayName == nil {
            fmt.Println("  Grantee:    EVERYONE")
        } else {
            fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
        }

        fmt.Println("  Type:      ", *g.Grantee.Type)
        fmt.Println("  Permission:", *g.Permission)
        fmt.Println("")
    }
    // snippet-end:[s3.go.get_bucket_acl.print]
}
// snippet-end:[s3.go.get_bucket_acl]
