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
// snippet-start:[s3.go.delete_bucket_website]
package main

// snippet-start:[s3.go.delete_bucket_website.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)

// snippet-end:[s3.go.delete_bucket_website.imports]

// RemoveBucketWebsite removes the website configuration for a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteBucketWebsite
func RemoveBucketWebsite(sess *session.Session, bucket *string) error {
    // snippet-start:[s3.go.delete_bucket_website.call]
    svc := s3.New(sess)

    _, err := svc.DeleteBucketWebsite(&s3.DeleteBucketWebsiteInput{
        Bucket: bucket,
    })
    // snippet-end:[s3.go.delete_bucket_website.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.delete_bucket_website.args]
    bucket := flag.String("b", "", "The name of the bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply the name of a bucket (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.delete_bucket_website.args]

    // snippet-start:[s3.go.delete_bucket_website.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.delete_bucket_website.session]

    err := RemoveBucketWebsite(sess, bucket)
    if err != nil {
        fmt.Println("Got an error removing the website configuration:")
        fmt.Println(err)
        return
    }

    fmt.Printf("Successfully deleted website configuration for " + *bucket)
}

// snippet-end:[s3.go.delete_bucket_website]
