// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Sets CORS permission on an S3 bucket.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[PutBucketCors function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[s3.go.set_cors.complete]
package main

// snippet-start:[s3.go.set_cors.imports]
import (
    "flag"
    "fmt"
    "os"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
)
// snippet-end:[s3.go.set_cors.imports]

// Configures CORS rules for a bucket by setting the allowed
// HTTP methods.
//
// Requires the bucket name, and can also take a space separated
// list of HTTP methods.
//
// Usage:
//    go run s3_set_cors.go -b BUCKET_NAME get put
func main() {
    // snippet-start:[s3.go.set_cors.vars]
    bucketPtr := flag.String("b", "", "Bucket to set CORS on, (required)")

    flag.Parse()

    if *bucketPtr == "" {
        exitErrorf("-b <bucket> Bucket name required")
    }

    methods := filterMethods(flag.Args())
    // snippet-end:[s3.go.set_cors.vars]

    // Initialize a session
    // snippet-start:[s3.go.set_cors.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := s3.New(sess)
    // snippet-end:[s3.go.set_cors.session]

    // Create a CORS rule for the bucket
    // snippet-start:[s3.go.set_cors.rule]
    rule := s3.CORSRule{
        AllowedHeaders: aws.StringSlice([]string{"Authorization"}),
        AllowedOrigins: aws.StringSlice([]string{"*"}),
        MaxAgeSeconds:  aws.Int64(3000),

        // Add HTTP methods CORS request that were specified in the CLI.
        AllowedMethods: aws.StringSlice(methods),
    }
    // snippet-end:[s3.go.set_cors.rule]

    // Create the parameters for the PutBucketCors API call, add add
    // the rule created to it.
    // snippet-start:[s3.go.set_cors.put]
    params := s3.PutBucketCorsInput{
        Bucket: bucketPtr,
        CORSConfiguration: &s3.CORSConfiguration{
            CORSRules: []*s3.CORSRule{&rule},
        },
    }

    _, err := svc.PutBucketCors(&params)
    if err != nil {
        // Print the error message
        exitErrorf("Unable to set Bucket %q's CORS, %v", *bucketPtr, err)
    }

    // Print the updated CORS config for the bucket
    fmt.Printf("Updated bucket %q CORS for %v\n", *bucketPtr, methods)
    // snippet-end:[s3.go.set_cors.put]
}

// snippet-start:[s3.go.set_cors.exit]
func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
// snippet-end:[s3.go.set_cors.exit]

// snippet-start:[s3.go.set_cors.filter]
func filterMethods(methods []string) []string {
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
// snippet-end:[s3.go.set_cors.filter]
// snippet-end:[s3.go.set_cors.complete]
