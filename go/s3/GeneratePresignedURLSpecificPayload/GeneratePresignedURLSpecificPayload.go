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
// snippet-start:[s3.go.generate_presigned_url_specific_payload]
package main

// snippet-start:[s3.go.generate_presigned_url_specific_payload.imports]
import (
    "flag"
    "fmt"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.generate_presigned_url_specific_payload.imports]

// GetPresignedURL creates a pre-signed URL for a bucket object
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the key of the object
//     content is the content of the object
// Output:
//     If success, the pre-signed URL, which is valid for 15 minutes, for the object and nil
//     Otherwise, an empty string and an error from the call to GetObjectRequest or Presign
func GetPresignedURL(sess *session.Session, bucket, key, content *string) (string, error) {
    // snippet-start:[s3.go.generate_presigned_url_specific_payload.call]
    svc := s3.New(sess)

    req, _ := svc.PutObjectRequest(&s3.PutObjectInput{
        Bucket: bucket,
        Key:    key,
        Body:   strings.NewReader(*content),
    })
    str, err := req.Presign(15 * time.Minute)
    // snippet-end:[s3.go.generate_presigned_url_specific_payload.call]
    if err != nil {
        return "", err
    }

    return str, nil
}

func main() {
    // snippet-start:[s3.go.generate_presigned_url_specific_payload.args]
    bucket := flag.String("b", "", "The bucket")
    key := flag.String("k", "", "The object key")
    content := flag.String("c", "", "The content of the object")
    flag.Parse()

    if *bucket == "" || *key == "" || *content == "" {
        fmt.Println("You must supply a bucket (-b BUCKET), key (-k KEY), and content (-c CONTENT)")
        return
    }
    // snippet-end:[s3.go.generate_presigned_url_specific_payload.args]

    // snippet-start:[s3.go.generate_presigned_url_specific_payload.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.generate_presigned_url_specific_payload.session]

    str, err := GetPresignedURL(sess, bucket, key, content)
    if err != nil {
        fmt.Println("Got an error retrieving the presigned URL:")
        fmt.Println(err)
        return
    }

    fmt.Println("The URL is:", str)
}
// snippet-end:[s3.go.generate_presigned_url_specific_payload]
