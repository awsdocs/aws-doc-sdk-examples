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
//snippet-start:[s3.go.put_object]
package main

//snippet-start:[s3.go.put_object.imports]
import (
    "flag"
    "fmt"
    "strings"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
//snippet-end:[s3.go.put_object.imports]

// PutObjectWithSetters uploads a file to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the name of the file
// Output:
//     If success, nil
//     Otherwise, an error from the call to PutObject
func PutObjectWithSetters(sess *session.Session, bucket *string, key *string) error {
    //snippet-start:[s3.go.put_object.call]
    svc := s3.New(sess)

    _, err := svc.PutObject((&s3.PutObjectInput{}).
        SetBucket(*bucket).
        SetKey(*key).
        SetBody(strings.NewReader("object body")), //.
    //      SetWebsiteRedirectLocation("https://example.com/something"),
    )
    //snippet-end:[s3.go.put_object.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    //snippet-start:[s3.go.put_object.args]
    bucket := flag.String("b", "", "The bucket to upload to")
    key := flag.String("k", "", "The object to upload")
    flag.Parse()

    if *bucket == "" || *key == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET) and key (-k KEY)")
        return
    }
    //snippet-end:[s3.go.put_object.args]

    //snippet-start:[s3.go.put_object.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    //snippet-end:[s3.go.put_object.session]

    err := PutObjectWithSetters(sess, bucket, key)
    if err != nil {
        fmt.Println("Got an error putting object:")
        fmt.Println(err)
        return
    }

    //snippet-start:[s3.go.put_object.print]
    fmt.Println("Put object with key " + *key + " into bucket" + *bucket)
    //snippet-end:[s3.go.put_object.print]
}
//snippet-end:[s3.go.put_object]
