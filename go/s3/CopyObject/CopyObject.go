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
// snippet-start:[s3.go.copy_object]
package main

// snippet-start:[s3.go.copy_object.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.copy_object.imports]

// CopyItem copies an item from one bucket to another
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     sourceBucket is the name of the source bucket
//     otherBucket is the name of the bucket to which the item is copied
//     item is the name of the bucket object to copy
// Output:
//     If success, nil
//     Otherwise, an error from the call to CopyObject or WaitUntilObjectExists
func CopyItem(sess *session.Session, sourceBucket *string, targetBucket *string, item *string) error {
    // snippet-start:[s3.go.copy_object.call]
    svc := s3.New(sess)
    source := *sourceBucket + "/" + *item

    // Copy the item
    _, err := svc.CopyObject(&s3.CopyObjectInput{
        Bucket:     targetBucket,
        CopySource: aws.String(source),
        Key:        item,
    })
    // snippet-end:[s3.go.copy_object.call]
    if err != nil {
        return err
    }

    // snippet-start:[s3.go.copy_object.wait]
    err = svc.WaitUntilObjectExists(&s3.HeadObjectInput{
        Bucket: targetBucket,
        Key:    item,
    })
    // snippet-end:[s3.go.copy_object.wait]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[s3.go.copy_object.args]
    sourceBucket := flag.String("f", "", "The bucket containing the object to copy")
    targetBucket := flag.String("t", "", "The bucket to which the object is copied")
    item := flag.String("i", "", "The object to copy")
    flag.Parse()

    if *sourceBucket == "" || *targetBucket == "" || *item == "" {
        fmt.Println("You must supply the bucket to copy from (-f BUCKET), to (-t BUCKET), and item to copy (-i ITEM")
        return
    }
    // snippet-end:[s3.go.copy_object.args]

    // snippet-start:[s3.go.copy_object.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.copy_object.session]

    err := CopyItem(sess, sourceBucket, targetBucket, item)
    if err != nil {
        fmt.Println("Got an error copying item:")
        fmt.Println(err)
        return
    }

    // snippet-start:[s3.go.copy_object.print]
    fmt.Println("Copied " + *item + " from " + *sourceBucket + " to " + *targetBucket)
    // snippet-end:[s3.go.copy_object.print]
}
// snippet-end:[s3.go.copy_object]
