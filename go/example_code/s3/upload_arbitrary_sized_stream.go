/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
        "compress/gzip"
        "io"
        "log"
        "os"

        "github.com/aws/aws-sdk-go/aws"
        "github.com/aws/aws-sdk-go/aws/session"
        "github.com/aws/aws-sdk-go/service/s3/s3manager"
    )

    func main() {
        file, err := os.Open("upload_file")
        if err != nil {
            log.Fatal("Failed to open file", err)
        }

        // Not required, but you could zip the file before uploading it
        // using io.Pipe read/writer to stream gzip'd file contents.
        reader, writer := io.Pipe()
        go func() {
            gw := gzip.NewWriter(writer)
            io.Copy(gw, file)

            file.Close()
            gw.Close()
            writer.Close()
        }()
        uploader := s3manager.NewUploader(session.New(&aws.Config{Region: aws.String("us-west-2")}))
        result, err := uploader.Upload(&s3manager.UploadInput{
            Body:   reader,
            Bucket: aws.String("myBucket"),
            Key:    aws.String("myKey"),
        })
        if err != nil {
            log.Fatalln("Failed to upload", err)
        }

        log.Println("Successfully uploaded to", result.Location)
    }