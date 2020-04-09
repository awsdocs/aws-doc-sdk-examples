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
// snippet-start:[s3.go.upload_stream]
package main

// snippet-start:[s3.go.upload_stream.imports]
import (
    "compress/gzip"
    "flag"
    "fmt"
    "io"
    "os"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)
// snippet-end:[s3.go.upload_stream.imports]

// UploadStream uploads a stream for a file to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the name of the object in the bucket
//     filename is the name of the file to stream to the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to Open or Upload 
func UploadStream(sess *session.Session, bucket *string, key *string, filename *string) error {
    // snippet-start:[s3.go.upload_stream.call]
    file, err := os.Open(*filename)
    if err != nil {
        return err
    }

    reader, writer := io.Pipe()

    go func() {
        gw := gzip.NewWriter(writer)
        _, err := io.Copy(gw, file)
        if err != nil {
            return
        }
        file.Close()
        gw.Close()
        writer.Close()
    }()

    uploader := s3manager.NewUploader(sess)

    _, err = uploader.Upload(&s3manager.UploadInput{
        Body:   reader,
        Bucket: bucket,
        Key:    key,
    })
    // snippet-end:[s3.go.upload_stream.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.upload_stream.args]
    bucket := flag.String("b", "", "The bucket to which the stream is uploaded")
    filename := flag.String("f", "", "The file to upload to the bucket")
    key := flag.String("k", "", "The name of the object in the bucket")
    flag.Parse()

    if *bucket == "" || *filename == "" || *key == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET), filename (-f FILENAME), and key value (-k KEY)")
        return
    }

    // snippet-end:[s3.go.upload_stream.args]

    // snippet-start:[s3.go.upload_stream.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.upload_stream.session]

    err := UploadStream(sess, bucket, key, filename)
    if err != nil {
        fmt.Println("Failed to upload " + *filename + " to bucket " + *bucket)
    }

    fmt.Println("Successfully uploaded " + *filename + " to " + *bucket)
}
// snippet-end:[s3.go.upload_stream]
