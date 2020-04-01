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

// snippet-start:[s3.go.upload_object]
package main

// snippet-start:[s3.go.upload_object.imports]
import (
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)
// snippet-end:[s3.go.upload_object.imports]

// PutFile uploads a file to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     filename is the name of the file
// Output:
//     If success, nil
//     Otherwise, an error from the call to Open or Upload
func PutFile(sess *session.Session, bucket *string, filename *string) error {
    // snippet-start:[s3.go.upload_object.call]
    file, err := os.Open(*filename)
    if err != nil {
        fmt.Println("Unable to open file " + *filename)
        return err
    }

    defer file.Close()

    uploader := s3manager.NewUploader(sess)

    _, err = uploader.Upload(&s3manager.UploadInput{
        Bucket: bucket,
        Key:    filename,
        Body:   file,
    })
    // snippet-end:[s3.go.upload_object.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.upload_object.args]
    bucket := flag.String("b", "", "The bucket to upload the file to")
    filename := flag.String("f", "", "The file to upload")
    flag.Parse()

    if *bucket == "" || *filename == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET) and file name (-f FILE)")
        return
    }
    // snippet-end:[s3.go.upload_object.args]

    // snippet-start:[s3.go.upload_object.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.upload_object.session]

    err := PutFile(sess, bucket, filename)
    if err != nil {
        fmt.Println("Got error uploading file:")
        fmt.Println(err)
        return
    }
}
// snippet-end:[s3.go.upload_object]
