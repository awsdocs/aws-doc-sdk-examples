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
// snippet-start:[s3.go.put_object_acl]
package main

// snippet-start:[s3.go.put_object_acl.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3iface"
)
// snippet-end:[s3.go.put_object_acl.imports]

// PutObjectACL gives the person with EMAIL address read access to BUCKET OBJECT
func PutObjectACL(svc s3iface.S3API, bucket, key, address *string) error {
    // snippet-start:[s3.go.put_object_acl.call]
    _, err := svc.PutObjectAcl(&s3.PutObjectAclInput{
        Bucket:    bucket,
        Key:       key,
        GrantRead: aws.String("emailaddress=" + *address),
    })
    // snippet-end:[s3.go.put_object_acl.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.put_object_acl.args]
    bucket := flag.String("b", "", "The bucket in which the object is stored")
    key := flag.String("k", "", "The key of the object")
    address := flag.String("a", "", "The email address")
    flag.Parse()

    if *bucket == "" || *key == "" || *address == "" {
        fmt.Println("You must supply a bucket (-b BUCKET), key (-k KEY), and email address (-a ADDRESS)")
        return
    }
    // snippet-end:[s3.go.put_object_acl.args]

    // snippet-start:[s3.go.put_object_acl.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := s3.New(sess)
    // snippet-end:[s3.go.put_object_acl.session]

    err := PutObjectACL(svc, bucket, key, address)
    if err != nil {
        fmt.Println("Got an error retrieving the ACL for " + *key)
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.put_object_acl.print]
    fmt.Println("Congratulations. You gave user with email address", address, "read permission to bucket", bucket, "object", key)
    // snippet-end:[s3.go.put_object_acl.print]
}
// snippet-end:[s3.go.put_object_acl]
