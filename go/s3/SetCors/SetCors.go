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
// snippet-start:[s3.go.put_bucket_cors.complete]
package main

// snippet-start:[s3.go.put_bucket_cors.imports]
import (
    "flag"
    "fmt"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.put_bucket_cors.imports]

// SetCors configures CORS rules for a bucket by setting the allowed HTTP methods.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     bucket is the name of bucket
//     methods are the HTTP methods: "POST", "GET", "PUT", "PATCH", "DELETE"
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
// Requires the bucket name, and can also take a space separated
// list of HTTP methods.
func SetCors(sess *session.Session, bucket *string, methods []string) error {
    // snippet-start:[s3.go.put_bucket_cors.rule]
    rule := s3.CORSRule{
        AllowedHeaders: aws.StringSlice([]string{"Authorization"}),
        AllowedOrigins: aws.StringSlice([]string{"*"}),
        MaxAgeSeconds:  aws.Int64(3000),
        AllowedMethods: aws.StringSlice(methods),
    }
    // snippet-end:[s3.go.put_bucket_cors.rule]

    // snippet-start:[s3.go.put_bucket_cors.call]
    svc := s3.New(sess)

    _, err := svc.PutBucketCors(&s3.PutBucketCorsInput{
        Bucket: bucket,
        CORSConfiguration: &s3.CORSConfiguration{
            CORSRules: []*s3.CORSRule{&rule},
        },
    })
    // snippet-end:[s3.go.put_bucket_cors.call]
    if err != nil {
        return err
    }

    return nil
}

// FilterMethods takes an array of strings and returns any that are HTTP methods
// snippet-start:[s3.go.put_bucket_cors.filter]
func FilterMethods(methods []string) []string {
    filtered := make([]string, 0, len(methods))
    for _, m := range methods {
        v := strings.ToUpper(m)
        switch v {
        case "POST", "GET", "PUT", "PATCH", "DELETE":
            filtered = append(filtered, v)
        }
    }

    return filtered
}

// snippet-end:[s3.go.put_bucket_cors.filter]

func main() {
    // snippet-start:[s3.go.put_bucket_cors.args]
    bucket := flag.String("b", "", "Bucket to set CORS on, (required)")

    flag.Parse()

    if *bucket == "" {
        fmt.Println("You must supply the name of a bucket (-b BUCKET)")
        return
    }

    methods := FilterMethods(flag.Args())
    if len(methods) == 0 {
        fmt.Println("You must supply at least one HTTP method: POST, GET, PUT, PATCH, or DELETE")
    }
    // snippet-end:[s3.go.put_bucket_cors.args]

    // snippet-start:[s3.go.put_bucket_cors.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[s3.go.put_bucket_cors.session]

    err := SetCors(sess, bucket, methods)
    if err != nil {
        fmt.Println("Got an error setting CORS:")
        fmt.Println(err)
        return
    }

}
// snippet-end:[s3.go.put_bucket_cors]
