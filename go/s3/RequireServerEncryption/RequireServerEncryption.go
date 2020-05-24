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
// snippet-start:[s3.go.require_server_encryption]
package main

// snippet-start:[s3.go.require_server_encryption.imports]
import (
    "encoding/json"
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.require_server_encryption.imports]

// AddKmsBucketPolicy adds a policy to enable KMS encryption by default on a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to json.Marshall or PutBucketPolicy
func AddKmsBucketPolicy(sess *session.Session, bucket *string) error {
    // snippet-start:[s3.go.require_server_encryption.policy]
    svc := s3.New(sess)

    PolicyDoc := map[string]interface{}{
        "Version": "2012-10-17",
        "Statement": []map[string]interface{}{
            {
                "Sid":       "DenyIncorrectEncryptionHeader",
                "Effect":    "Deny",
                "Principal": "*",
                "Action":    "s3:PutObject",
                "Resource":  "arn:aws:s3:::" + *bucket + "/*",
                "Condition": map[string]interface{}{
                    "StringNotEquals": map[string]interface{}{
                        "s3:x-amz-server-side-encryption": "aws:kms",
                    },
                },
            },
            {
                "Sid":       "DenyUnEncryptedObjectUploads",
                "Effect":    "Deny",
                "Principal": "*",
                "Action":    "s3:PutObject",
                "Resource":  "arn:aws:s3:::" + *bucket + "/*",
                "Condition": map[string]interface{}{
                    "Null": map[string]interface{}{
                        "s3:x-amz-server-side-encryption": "true",
                    },
                },
            },
        },
    }

    policy, err := json.Marshal(PolicyDoc)
    // snippet-end:[s3.go.require_server_encryption.policy]
    if err != nil {
        return err
    }

    // snippet-start:[s3.go.require_server_encryption.call]
    _, err = svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
        Bucket: bucket,
        Policy: aws.String(string(policy)),
    })
    // snippet-end:[s3.go.require_server_encryption.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.require_server_encryption.args]
    bucket := flag.String("b", "", "The bucket to encrypt")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.require_server_encryption.args]

    // snippet-start:[s3.go.require_server_encryption.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.require_server_encryption.session]

    err := AddKmsBucketPolicy(sess, bucket)
    if err != nil {
        fmt.Println("Got an error adding policy to bucket " + *bucket + ":")
        fmt.Println(err)
        return
    }

    fmt.Println("Set policy for " + *bucket)
}
// snippet-end:[s3.go.require_server_encryption]
