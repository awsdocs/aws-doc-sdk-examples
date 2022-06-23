// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Lists the AWS CloudTrail trails.]
// snippet-keyword:[AWS CloudTrail]
// snippet-keyword:[DescribeTrails function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cloudtrail]
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
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
)

func main() {
    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create CloudTrail client
    svc := cloudtrail.New(sess)

    resp, err := svc.DescribeTrails(&cloudtrail.DescribeTrailsInput{TrailNameList: nil})
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Found", len(resp.TrailList), "trail(s)")
    fmt.Println("")

    for _, trail := range resp.TrailList {
        fmt.Println("Trail name:  " + *trail.Name)
        fmt.Println("Bucket name: " + *trail.S3BucketName)
        fmt.Println("")
    }
}
