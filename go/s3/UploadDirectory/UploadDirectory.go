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

// snippet-start:[s3.go.upload_directory]
package main

// snippet-start:[s3.go.upload_directory.imports]
import (
    "flag"
    "fmt"
    "os"
    "path/filepath"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)
// snippet-end:[s3.go.upload_directory.imports]

// DirectoryIterator represents an iterator of a specified directory
type DirectoryIterator struct {
    filePaths []string
    bucket    string
    next      struct {
        path string
        f    *os.File
    }
    err error
}

// const exitError = 1

// UploadDirectory uploads the files in a directory to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     path is the path to the directory to upload
// Output:
//     If success, nil
//     Otherwise, an error from the call to UploadWithIterator
func UploadDirectory(sess *session.Session, bucket *string, path *string) error {
    di := NewDirectoryIterator(bucket, path)
    uploader := s3manager.NewUploader(sess)

    err := uploader.UploadWithIterator(aws.BackgroundContext(), di)
    if err != nil {
        return err
    }

    return nil
}

// NewDirectoryIterator builds a new DirectoryIterator
func NewDirectoryIterator(bucket *string, dir *string) s3manager.BatchUploadIterator {
    var paths []string
    filepath.Walk(*dir, func(path string, info os.FileInfo, err error) error {
        if !info.IsDir() {
            paths = append(paths, path)
        }
        return nil
    })

    return &DirectoryIterator{
        filePaths: paths,
        bucket:    *bucket,
    }
}

// Next returns whether next file exists or not
func (di *DirectoryIterator) Next() bool {
    if len(di.filePaths) == 0 {
        di.next.f = nil
        return false
    }

    f, err := os.Open(di.filePaths[0])
    di.err = err
    di.next.f = f
    di.next.path = di.filePaths[0]
    di.filePaths = di.filePaths[1:]

    return true && di.Err() == nil
}

// Err returns error of DirectoryIterator
func (di *DirectoryIterator) Err() error {
    return di.err
}

// UploadObject uploads a file
func (di *DirectoryIterator) UploadObject() s3manager.BatchUploadObject {
    f := di.next.f
    return s3manager.BatchUploadObject{
        Object: &s3manager.UploadInput{
            Bucket: &di.bucket,
            Key:    &di.next.path,
            Body:   f,
        },
        After: func() error {
            return f.Close()
        },
    }
}

func main() {
    // snippet-start:[s3.go.upload_directory.args]
    bucket := flag.String("b", "", "The name of the bucket")
    directory := flag.String("d", "", "The directory to upload")
    flag.Parse()

    if *bucket == "" || *directory == "" {
        fmt.Println("You must supply the bucket name (-b BUCKET) and directory path (-d DIRECTORY)")
        return
    }
    // snippet-end:[s3.go.upload_directory.args]

    // snippet-start:[s3.go.upload_directory.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.upload_directory.session]

    err := UploadDirectory(sess, bucket, directory)
    if err != nil {
        fmt.Println("Got an error uploading directory " + *directory + " to bucket " + *bucket)
        return
    }

    fmt.Println("Uploaded directory " + *directory + " to bucket " + *bucket)
}
// snippet-end:[s3.go.upload_directory]
