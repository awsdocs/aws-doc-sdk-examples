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
// snippet-start:[s3.go.put_bucket_website]
package main

// snippet-start:[s3.go.put_bucket_website.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.put_bucket_website.imports]

// SetWebPage sets up a bucket as a static web site
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     indexPage is the name of the index page
//     errorPage is the name of the error page
// Output:
//     If success, nil
//     Otherwise, an error from the call to PutBucketWebsite
func SetWebPage(sess *session.Session, bucket, indexPage, errorPage *string) error {
    // snippet-start:[s3.go.put_bucket_website.call]
    svc := s3.New(sess)

    params := s3.PutBucketWebsiteInput{
        Bucket: bucket,
        WebsiteConfiguration: &s3.WebsiteConfiguration{
            IndexDocument: &s3.IndexDocument{
                Suffix: indexPage,
            },
        },
    }

    if len(*errorPage) > 0 {
        params.WebsiteConfiguration.ErrorDocument = &s3.ErrorDocument{
            Key: errorPage,
        }
    }

    _, err := svc.PutBucketWebsite(&params)
    // snippet-end:[s3.go.put_bucket_website.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.put_bucket_website.args]
    bucket := flag.String("b", "", "The name of the bucket")
    indexPage := flag.String("i", "", "The name of the index page")
    errorPage := flag.String("e", "", "The name of the error page (optional)")
    flag.Parse()

    if *bucket == "" || *indexPage == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET) and index page (-i INDEX)")
        return
    }
    // snippet-end:[s3.go.put_bucket_website.args]

    // snippet-start:[s3.go.put_bucket_website.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.put_bucket_website.session]

    err := SetWebPage(sess, bucket, indexPage, errorPage)
    if err != nil {
        fmt.Println("Got an error setting bucket as static web site:")
        fmt.Println(err)
        return
    }

    fmt.Println("Set bucket website configuration")
}
// snippet-end:[s3.go.put_bucket_website]
