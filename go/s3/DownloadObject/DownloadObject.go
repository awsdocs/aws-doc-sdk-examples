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

// snippet-start: [s3.go.download_object]
package main

// snippet-start: [s3.go.download_object.imports]
import (
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)
// snippet-end: [s3.go.download_object.imports]

// DownloadObject downloads a file from a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     filename is the name of the file
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to Create or Download
func DownloadObject(sess *session.Session, filename *string, bucket *string) error {
    // snippet-start: [s3.go.download_object.create]
    file, err := os.Create(*filename)
    // snippet-end: [s3.go.download_object.create]
    if err != nil {
        return err
    }

    defer file.Close()
    
    // snippet-start: [s3.go.download_object.call]
    downloader := s3manager.NewDownloader(sess)

    _, err = downloader.Download(file,
        &s3.GetObjectInput{
            Bucket: bucket,
            Key:    filename,
        })
    // snippet-end: [s3.go.download_object.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start: [s3.go.download_object.args]
    bucket := flag.String("b", "", "The bucket to download from")
    filename := flag.String("f", "", "The name of the file to download")
    flag.Parse()

    if *bucket == "" || *filename == "" {
        fmt.Println("You must specify a bucket name (-b BUCKET) and filename (-f FILENAME")
        return
    }
    // snippet-end: [s3.go.download_object.args]

    // snippet-start: [s3.go.download_object.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end: [s3.go.download_object.session]

    err := DownloadObject(sess, filename, bucket)
    if err != nil {
        fmt.Println("Got error downloading " + *filename + ":")
        fmt.Println(err)
        return
    }

    fmt.Println("Downloaded " + *filename)
}
// snippet-end: [s3.go.download_object]
