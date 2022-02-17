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
// snippet-start:[s3.go.set_default_encryption]
package main

// snippet-start:[s3.go.set_default_encryption.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.set_default_encryption.imports]

// AddKmsEncryption enforces encryption using a KMS key on a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     kmsKeyID is the ID of a KMS key
// Output:
//     If success, nil
//     Otherwise, an error from the call to PutBucketEncryption
func AddKmsEncryption(sess *session.Session, bucket, kmsKeyID *string) error {
    // snippet-start:[s3.go.set_default_encryption.call]
    svc := s3.New(sess)

    // Encrypt with KMS by default
    defEnc := &s3.ServerSideEncryptionByDefault{
        KMSMasterKeyID: kmsKeyID,
        SSEAlgorithm:   aws.String(s3.ServerSideEncryptionAwsKms),
    }
    rule := &s3.ServerSideEncryptionRule{ApplyServerSideEncryptionByDefault: defEnc}
    rules := []*s3.ServerSideEncryptionRule{rule}
    serverConfig := &s3.ServerSideEncryptionConfiguration{Rules: rules}

    _, err := svc.PutBucketEncryption(&s3.PutBucketEncryptionInput{
        Bucket:                            bucket,
        ServerSideEncryptionConfiguration: serverConfig,
    })
    // snippet-end:[s3.go.set_default_encryption.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.set_default_encryption.args]
    bucket := flag.String("b", "", "The bucket")
    kmsKeyID := flag.String("k", "", "The KMS key ID")
    flag.Parse()

    if *bucket == "" || *kmsKeyID == "" {
        fmt.Println("You must supply bucket (-b BUCKET) and KMS key ID (-k KEY)")
        return
    }
    // snippet-end:[s3.go.set_default_encryption.args]

    // snippet-start:[s3.go.set_default_encryption.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.set_default_encryption.session]

    err := AddKmsEncryption(sess, bucket, kmsKeyID)
    if err != nil {
        fmt.Println("Got an error adding default KMS encryption to bucket:")
        fmt.Println(err)
        return
    }

    fmt.Println("Bucket " + *bucket + " now has KMS encryption by default")
}
// snippet-end:[s3.go.set_default_encryption]
