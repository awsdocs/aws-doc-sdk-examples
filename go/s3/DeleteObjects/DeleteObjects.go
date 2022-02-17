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
// snippet-start:[s3.go.delete_objects]
package main

// snippet-start:[s3.go.delete_objects.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)
// snippet-end:[s3.go.delete_objects.imports]

// DeleteItems deletes all of the objects in the bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to NewBatchDeleteWithClient
func DeleteItems(sess *session.Session, bucket *string) error {
    // snippet-start:[s3.go.delete_objects.call]
    svc := s3.New(sess)

    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: bucket,
    })

    err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
    // snippet-end:[s3.go.delete_objects.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.delete_objects.args]
    bucket := flag.String("b", "", "The bucket to empty")
    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply a bucket to empty (-b BUCKET)")
        return
    }
    // snippet-end:[s3.go.delete_objects.args]

    // snippet-start:[s3.go.delete_objects.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.delete_objects.session]

    err := DeleteItems(sess, bucket)
    if err != nil {
        fmt.Println("Got an error deleting items:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.delete_objects.print]
    fmt.Println("Deleted object(s) from " + *bucket)
    // snippet-end:[s3.go.delete_objects.print]
}
// snippet-end:[s3.go.delete_objects]
