// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[CloudWatchGetLogEvents.go lists up to 100 of the latest events for a log group's log stream.]
// snippet-keyword:[AWS CloudWatch]
// snippet-keyword:[GetLogEvents function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cloudwatch]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-3-4]
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
// snippet-start:[cloudwatch.go.getlogevents]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchlogs"

    "fmt"
    "os"
)

func main() {
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := cloudwatchlogs.New(sess)

    // Get up to the last 100 log events for LOG-STREAM-NAME
    // in LOG-GROUP-NAME:
    resp, err := svc.GetLogEvents(&cloudwatchlogs.GetLogEventsInput{
        Limit:         aws.Int64(100),
        LogGroupName:  aws.String("LOG-GROUP-NAME"),
        LogStreamName: aws.String("LOG-STREAM-NAME"),
    })
    if err != nil {
        fmt.Println("Got error getting log events:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Event messages for stream LOG-STREAM-NAME in log group LOG-GROUP-NAME:")

    gotToken := ""
    nextToken := ""

    for _, event := range resp.Events {
        gotToken = nextToken
        nextToken = *resp.NextForwardToken

        if gotToken == nextToken {
            break
        }

        fmt.Println("  ", *event.Message)
    }
}
// snippet-end:[cloudwatch.go.getlogevents]
