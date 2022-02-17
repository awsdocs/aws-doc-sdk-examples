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
// snippet-start:[s3.go.delete_object]
package main

// snippet-start:[s3.go.delete_object.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.delete_object.imports]

// DeleteItem deletes an item from a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     item is the name of the bucket object to delete
// Output:
//     If success, nil
//     Otherwise, an error from the call to DeleteObject or WaitUntilObjectNotExists
func DeleteItem(sess *session.Session, bucket *string, item *string) error {
    // snippet-start:[s3.go.delete_object.call]
    svc := s3.New(sess)

    _, err := svc.DeleteObject(&s3.DeleteObjectInput{
        Bucket: bucket,
        Key:    item,
    })
    // snippet-end:[s3.go.delete_object.call]
    if err != nil {
        return err
    }

    // snippet-start:[s3.go.delete_object.wait]
    err = svc.WaitUntilObjectNotExists(&s3.HeadObjectInput{
        Bucket: bucket,
        Key:    item,
    })
    // snippet-end:[s3.go.delete_object.wait]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.delete_object.args]
    bucket := flag.String("b", "", "The bucket from which the object is deleted")
    item := flag.String("i", "", "The object to delete")
    flag.Parse()

    if *bucket == "" || *item == "" {
        fmt.Println("You must supply the bucket (-b BUCKET), and item to delete (-i ITEM")
        return
    }
    // snippet-end:[s3.go.delete_object.args]

    // snippet-start:[s3.go.delete_object.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.delete_object.session]

    err := DeleteItem(sess, bucket, item)
    if err != nil {
        fmt.Println("Got an error deleting item:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.delete_object.print]
    fmt.Println("Deleted " + *item + " from " + *bucket)
    // snippet-end:[s3.go.delete_object.print]
}

// snippet-end:[s3.go.delete_object]
