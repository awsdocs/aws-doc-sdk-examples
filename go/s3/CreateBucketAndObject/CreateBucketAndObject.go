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
// snippet-start:[s3.go.create_bucket_and_object]
package main

// snippet-start:[s3.go.create_bucket_and_object.imports]
import (
    "flag"
    "fmt"
    "strings"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.create_bucket_and_object.imports]

// CreateBucketAndObject creates an Amazon S3 bucket and a dummy object in that bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the name of the object
// Output:
//     If success, nil
//     Otherwise, an error from the call to CreateBucket, WaitUntilBucketExists, or PutObject
func CreateBucketAndObject(sess *session.Session, bucket *string, key *string) error {
    // snippet-start:[s3.go.create_bucket_and_object.call]
    svc := s3.New(sess)

    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.create_bucket_and_object.call]
    if err != nil {
        fmt.Println("Got error trying to create bucket:")
        return err
    }

    // snippet-start:[s3.go.create_bucket_and_object.wait]
    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{Bucket: bucket})
    // snippet-end:[s3.go.create_bucket_and_object.wait]
    if err != nil {
        fmt.Println("Got error waiting for bucket:")
        return err
    }

    // snippet-start:[s3.go.create_bucket_and_object.put_object]
    _, err = svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: bucket,
        Key:    key,
    })
    // snippet-end:[s3.go.create_bucket_and_object.put_object]
    if err != nil {
        fmt.Println("Got error putting object in bucket:")
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.create_bucket_and_object.put_args]
    bucket := flag.String("b", "", "The name of the bucket")
    key := flag.String("k", "TestFile.txt", "The name of the object (key)")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply the name of the bucket (-b BUCKET)")
    }
    // snippet-end:[s3.go.create_bucket_and_object.put_args]

    // snippet-start:[s3.go.create_bucket_and_object.put_session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.create_bucket_and_object.put_session]

    err := CreateBucketAndObject(sess, bucket, key)
    if err != nil {
        fmt.Println("err")
        return
    }

    fmt.Println("Successfully created bucket " + *bucket + " and uploaded data with key " + *key)
}
// snippet-end:[s3.go.create_bucket_and_object]