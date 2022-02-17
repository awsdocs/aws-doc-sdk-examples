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
// snippet-start:[s3.go.get_object_acl]
package main

// snippet-start:[s3.go.get_object_acl.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.get_object_acl.imports]

// GetObjectACL gets the ACL for a bucket object
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the name of the object
// Output:
//     If success, the ACL of the object and nil
//     Otherwise, nil and an error from the call to GetObjectAcl
func GetObjectACL(sess *session.Session, bucket, key *string) (*s3.GetObjectAclOutput, error) {
    // snippet-start:[s3.go.get_object_acl.call]
    svc := s3.New(sess)

    result, err := svc.GetObjectAcl(&s3.GetObjectAclInput{
        Bucket: bucket,
        Key:    key,
    })
    // snippet-end:[s3.go.get_object_acl.call]
    if err != nil {
        return nil, err
    }
    return result, nil
}

func main() {
    // snippet-start:[s3.go.get_object_acl.args]
    bucket := flag.String("b", "", "The bucket containing the object")
    key := flag.String("k", "", "The bucket object to get ACL from")
    flag.Parse()

    if *bucket == "" || *key == "" {
        fmt.Println("You must supply a bucket (-b BUCKET) and item key (-k ITEM)")
        return
    }
    // snippet-end:[s3.go.get_object_acl.args]

    // snippet-start:[s3.go.get_object_acl.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.get_object_acl.session]

    result, err := GetObjectACL(sess, bucket, key)
    if err != nil {
        fmt.Println("Got an error getting ACL for " + *key)
        return
    }

    // snippet-start:[s3.go.get_object_acl.print]
    fmt.Println("Owner:", *result.Owner.DisplayName)
    fmt.Println("")
    fmt.Println("Grants")

    for _, g := range result.Grants {
        fmt.Println("  Grantee:   ", *g.Grantee.DisplayName)
        fmt.Println("  Type:      ", *g.Grantee.Type)
        fmt.Println("  Permission:", *g.Permission)
        fmt.Println("")
    }
    // snippet-end:[s3.go.get_object_acl.print]
}
// snippet-end:[s3.go.get_object_acl]
