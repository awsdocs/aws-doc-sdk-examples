/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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

    "fmt"
)

// Tag S3 bucket MyBucket with cost center tag "123456" and stack tag "MyTestStack".
//
// See:
//    http://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/cost-alloc-tags.html
func main() {
    // Pre-defined values
    bucket := "MyBucket"
    tagName1 := "Cost Center"
    tagValue1 := "123456"
    tagName2 := "Stack"
    tagValue2 := "MyTestStack"
    
    // Initialize a session in us-west-2 that the SDK will use to load credentials
    // from the shared credentials file. (~/.aws/credentials).
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    // Create S3 service client
    svc := s3.New(sess)

    // Create input for PutBucket method
    input := &s3.PutBucketTaggingInput{
        Bucket: aws.String(bucket),
        Tagging: &s3.Tagging{
            TagSet: []*s3.Tag{
                {
                    Key:   aws.String(tagName1),
                    Value: aws.String(tagValue1),
                },
                {
                    Key:   aws.String(tagName2),
                    Value: aws.String(tagValue2),
              },
            },
        },
    }

    _, err = svc.PutBucketTagging(input)
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    // Now show the tags
    // Create input for GetBucket method
    input := &s3.GetBucketTaggingInput{
        Bucket: aws.String(bucket),
    }

    result, err := svc.GetBucketTagging(input)
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    numTags := len(result.TagSet)

    if numTags > 0 {
        fmt.Println("Found", numTags, "Tag(s):")
        fmt.Println("")

        for _, t := range result.TagSet {
            fmt.Println("  Key:  ", *t.Key)
            fmt.Println("  Value:", *t.Value)
            fmt.Println("")
        }
    } else {
        fmt.Println("Did not find any tags")
    }
}
