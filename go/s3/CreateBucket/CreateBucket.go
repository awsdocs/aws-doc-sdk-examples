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
// snippet-start:[s3.go.create_bucket]
package main

// snippet-start:[s3.go.create_bucket.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.create_bucket.imports]

// MakeBucket creates a bucket.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateBucket
func MakeBucket(sess *session.Session, bucket *string) error {
    // snippet-start:[s3.go.create_bucket.call]
    svc := s3.New(sess)

    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.create_bucket.call]
    if err != nil {
        return err
    }

    // snippet-start:[s3.go.create_bucket.wait]
    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.create_bucket.wait]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.create_bucket.args]
    bucket := flag.String("b", "", "The name of the bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.create_bucket.args]

    // snippet-start:[s3.go.create_bucket.imports.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.create_bucket.imports.session]

    err := MakeBucket(sess, bucket)
    if err != nil {
        fmt.Println("Could not create bucket " + *bucket)
    }
}
// snippet-end:[s3.go.create_bucket]
