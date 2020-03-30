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
// snippet-start:[s3.go.list_object]
package main

// snippet-start:[s3.go.list_object.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.list_object.imports]

// GetObjects retrieves the objects in an Amazon S3 bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, the list of objects and nil
//     Otherwise, nil and an error from the call to ListObjectsV2
func GetObjects(sess *session.Session, bucket *string) (*s3.ListObjectsV2Output, error) {
    svc := s3.New(sess)

    // Get the list of items
    resp, err := svc.ListObjectsV2(&s3.ListObjectsV2Input{Bucket: bucket})
    if err != nil {
        return nil, err
    }

    return resp, nil
}

// Lists the items in the specified S3 Bucket
//
// Usage:
//    go run s3_list_objects.go BUCKET_NAME
func main() {
    // snippet-start:[s3.go.list_objects.args]
    bucket := flag.String("b", "", "The name of the bucket")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply the name of a bucket (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.list_objects.args]

    // snippet-start:[s3.go.list_objects.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-start:[s3.go.list_objects.session]

    resp, err := GetObjects(sess, bucket)
    if err != nil {
        fmt.Println("Got error retrieving list of objects:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.list_objects.print]
    fmt.Println("Objects in " + *bucket + ":")

    for _, item := range resp.Contents {
        fmt.Println("Name:          ", *item.Key)
        fmt.Println("Last modified: ", *item.LastModified)
        fmt.Println("Size:          ", *item.Size)
        fmt.Println("Storage class: ", *item.StorageClass)
        fmt.Println("")
    }

    fmt.Println("Found", len(resp.Contents), "items in bucket", bucket)
    fmt.Println("")
    // snippet-end:[s3.go.list_objects.print]
}
// snippet-end:[s3.go.list_objects]
