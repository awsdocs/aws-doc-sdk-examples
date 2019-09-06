// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Uploads a file to an S3 bucket.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[s3manager.NewUploader]
// snippet-keyword:[Upload function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
    "fmt"
    "os"
)

// Creates a S3 Bucket in the region configured in the shared config
// or AWS_REGION environment variable.
//
// Usage:
//    go run s3_upload_object.go BUCKET_NAME FILENAME
func main() {
    if len(os.Args) != 3 {
        exitErrorf("bucket and file name required\nUsage: %s bucket_name filename",
            os.Args[0])
    }

    bucket := os.Args[1]
    filename := os.Args[2]

    file, err := os.Open(filename)
    if err != nil {
        exitErrorf("Unable to open file %q, %v", err)
    }

    defer file.Close()

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Setup the S3 Upload Manager. Also see the SDK doc for the Upload Manager
    // for more information on configuring part size, and concurrency.
    //
    // http://docs.aws.amazon.com/sdk-for-go/api/service/s3/s3manager/#NewUploader
    uploader := s3manager.NewUploader(sess)

    // Upload the file's body to S3 bucket as an object with the key being the
    // same as the filename.
    _, err = uploader.Upload(&s3manager.UploadInput{
        Bucket: aws.String(bucket),

        // Can also use the `filepath` standard library package to modify the
        // filename as need for an S3 object key. Such as turning absolute path
        // to a relative path.
        Key: aws.String(filename),

        // The file to be uploaded. io.ReadSeeker is preferred as the Uploader
        // will be able to optimize memory when uploading large content. io.Reader
        // is supported, but will require buffering of the reader's bytes for
        // each part.
        Body: file,
    })
    if err != nil {
        // Print the error and exit.
        exitErrorf("Unable to upload %q to %q, %v", filename, bucket, err)
    }

    fmt.Printf("Successfully uploaded %q to %q\n", filename, bucket)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
