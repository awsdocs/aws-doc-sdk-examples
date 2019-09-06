// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Generates a pre-signed URL for a PUT operation that checks whether the expected content was uploaded.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[PutObjectRequest function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "log"
    "strings"
    "time"
)

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create S3 service client
    svc := s3.New(sess)

    req, _ := svc.PutObjectRequest(&s3.PutObjectInput{
        Bucket: aws.String("myBucket"),
        Key:    aws.String("myKey"),
        Body:   strings.NewReader("EXPECTED CONTENTS"),
    })
    str, err := req.Presign(15 * time.Minute)

    log.Println("The URL is:", str, " err:", err)
}
