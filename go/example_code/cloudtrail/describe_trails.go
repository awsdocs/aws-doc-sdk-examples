/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
)

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load configuration,
    // and credentials from the shared config file ~/.aws/config.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create CloudTrail client
    svc := cloudtrail.New(sess)

    resp, err := svc.DescribeTrails(&cloudtrail.DescribeTrailsInput{TrailNameList: nil})

    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Found",len(resp.TrailList),"trail(s) in", regionName)
    fmt.Println("")

    for _, trail := range resp.TrailList {
        fmt.Println("Trail name:  " + *trail.Name)
        fmt.Println("Bucket name: " + *trail.S3BucketName)
        fmt.Println("")
    }
}
