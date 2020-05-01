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
// snippet-start: [s3.go.enforce_md5]
package main

// snippet-start: [s3.go.enforce_md5.imports]
import (
    "crypto/md5"
    "encoding/base64"
    "flag"
    "fmt"
    "net/http"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end: [s3.go.enforce_md5.imports]

// SetMd5 enforces an MD5 checksum on the object uploaded to a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of the bucket
//     key is the key of the object
// Output:
//     If success, an HTTP response object and nil
//     Otherwise, nil and an error from the call to PutObjectRequest
func SetMd5(sess *session.Session, bucket, key *string) (*http.Response, error) {
    // snippet-start: [s3.go.enforce_md5.client_md5]
    svc := s3.New(sess)

    h := md5.New()
    content := strings.NewReader("")
    _, err := content.WriteTo(h)
    // snippet-end: [s3.go.enforce_md5.client_md5]
    if err != nil {
        return nil, err
    }

    // snippet-start: [s3.go.enforce_md5.put_object]
    resp, _ := svc.PutObjectRequest(&s3.PutObjectInput{
        Bucket: bucket,
        Key:    key,
    })

    md5s := base64.StdEncoding.EncodeToString(h.Sum(nil))
    resp.HTTPRequest.Header.Set("Content-MD5", md5s)

    url, err := resp.Presign(15 * time.Minute)
    // snippet-end: [s3.go.enforce_md5.put_object]
    if err != nil {
        return nil, err
    }

    // snippet-start: [s3.go.enforce_md5.new_request]
    req, err := http.NewRequest("PUT", url, strings.NewReader(""))
    req.Header.Set("Content-MD5", md5s)
    // snippet-end: [s3.go.enforce_md5.new_request]
    if err != nil {
        return nil, err
    }

    // snippet-start: [s3.go.enforce_md5.default_client]
    defClient, err := http.DefaultClient.Do(req)
    // snippet-end: [s3.go.enforce_md5.default_client]
    if err != nil {
        return nil, err
    }

    return defClient, nil
}

func main() {
    // snippet-start: [s3.go.enforce_md5.args]
    bucket := flag.String("b", "", "The name of the bucket")
    key := flag.String("k", "", "The object key")
    flag.Parse()

    if *bucket == "" || *key == "" {
        fmt.Println("You must supply a bucket name (-b BUCKET) and key name (-k KEY)")
        return
    }
    // snippet-end: [s3.go.enforce_md5.args]

    // snippet-start: [s3.go.enforce_md5.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end: [s3.go.enforce_md5.session]

    defClient, err := SetMd5(sess, bucket, key)
    if err != nil {
        fmt.Println("Got an error setting MD5:")
        fmt.Println(err)
        return
    }

    fmt.Println(defClient, err)
}
// snippet-end: [s3.go.enforce_md5]
