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
// snippet-start:[s3.go.encrypt_on_server_with_kms]
package main

// snippet-start:[s3.go.encrypt_on_server_with_kms.imports]
import (
    "flag"
    "fmt"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.encrypt_on_server_with_kms.imports]

// AddObjectWithServerKms adds an object to a bucket with encryption based on a KMS key
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     object is the name of the object to upload to the bucket
//     kmsKey is the KMS key used to encrypt the object in the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to PutObject
func AddObjectWithServerKms(sess *session.Session, bucket, object, kmsKey *string) error {
    // snippet-start:[s3.go.encrypt_on_server_with_kms.call]
    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:                 strings.NewReader(*object),
        Bucket:               bucket,
        Key:                  object,
        ServerSideEncryption: aws.String("aws:kms"),
        SSEKMSKeyId:          kmsKey,
    })
    // snippet-end:[s3.go.encrypt_on_server_with_kms.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.encrypt_on_server_with_kms.args]
    bucket := flag.String("b", "", "The bucket")
    object := flag.String("o", "", "The file to upload")
    kmsKey := flag.String("k", "", "The KMS key to encrypt with")
    flag.Parse()

    if *bucket == "" || *object == "" || *kmsKey == "" {
        fmt.Println("You must supply a bucket (-b BUCKET), object to upload (-o OBJECT), and KMS key (-k KEY)")
        return
    }
    // snippet-end:[s3.go.encrypt_on_server_with_kms.args]

    // snippet-start:[s3.go.encrypt_on_server_with_kms.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.encrypt_on_server_with_kms.session]

    err := AddObjectWithServerKms(sess, bucket, object, kmsKey)
    if err != nil {
        fmt.Println("Got an error adding object to bucket:")
        fmt.Println(err)
        return
    }

    fmt.Println("Added object " + *object + " to bucket " + *bucket + " with AWS KMS encryption")
}
// snippet-end:[s3.go.encrypt_on_server_with_kms]
