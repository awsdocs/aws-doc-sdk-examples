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
// snippet-start:[s3.go.list_buckets]
package main

// snippet-start:[s3.go.list_buckets.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.list_buckets.imports]

// GetAllBuckets retrieves a list of all buckets.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of buckets and nil
//     Otherwise, nil and an error from the call to ListBuckets
func GetAllBuckets(sess *session.Session) (*s3.ListBucketsOutput, error) {
    // snippet-start:[s3.go.list_buckets.imports.call]
    svc := s3.New(sess)

    result, err := svc.ListBuckets(&s3.ListBucketsInput{})
    // snippet-end:[s3.go.list_buckets.imports.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[s3.go.list_buckets.imports.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.list_buckets.imports.session]
    
    result, err := GetAllBuckets(sess)
    if err != nil {
        fmt.Println("Got an error retrieving buckets:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.list_buckets.imports.print]
    fmt.Println("Buckets:")

    for _, bucket := range result.Buckets {
        fmt.Println(*bucket.Name + ": " + bucket.CreationDate.Format("2006-01-02 15:04:05 Monday"))
    }
    // snippet-end:[s3.go.list_buckets.imports.print]
}
// snippet-end:[s3.go.list_buckets]