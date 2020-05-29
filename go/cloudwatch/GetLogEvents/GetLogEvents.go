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
// snippet-start:[cloudwatch.go.getlogevents]
package main

// snippet-start:[cloudwatch.go.getlogevents.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchlogs"
)
// snippet-end:[cloudwatch.go.getlogevents.imports]

// GetLogEvents retrieves CloudWatchLog events.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     limit is the maximum number of log events to retrieve
//     logGroupName is the name of the log group
//     logStreamName is the name of the log stream
// Output:
//     If success, a GetLogEventsOutput object containing the events and nil
//     Otherwise, nil and an error from the call to GetLogEvents
func GetLogEvents(sess *session.Session, limit *int64, logGroupName *string, logStreamName *string) (*cloudwatchlogs.GetLogEventsOutput, error) {
    // snippet-start:[cloudwatch.go.getlogevents.call]
    svc := cloudwatchlogs.New(sess)

    resp, err := svc.GetLogEvents(&cloudwatchlogs.GetLogEventsInput{
        Limit:         limit,
        LogGroupName:  logGroupName,
        LogStreamName: logStreamName,
    })
    // snippet-end:[cloudwatch.go.getlogevents.call]
    if err != nil {
        return nil, err
    }

    return resp, nil
}

func main() {
    // snippet-start:[cloudwatch.go.getlogevents.args]
    limit := flag.Int64("l", 100, "The maximum number of events to retrieve")
    logGroupName := flag.String("g", "", "The name of the log group")
    logStreamName := flag.String("s", "", "The name of the log stream")
    flag.Parse()

    if *logGroupName == "" || *logStreamName == "" {
        fmt.Println("You must supply a log group name (-g LOG-GROUP) and log stream name (-s LOG-STREAM)")
        return
    }
    // snippet-end:[cloudwatch.go.getlogevents.args]

    // snippet-start:[cloudwatch.go.getlogevents.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudwatch.go.getlogevents.session]

    resp, err := GetLogEvents(sess, limit, logGroupName, logStreamName)
    if err != nil {
        fmt.Println("Got error getting log events:")
        fmt.Println(err)
        return
    }

    // snippet-start:[cloudwatch.go.getlogevents.print]
    fmt.Println("Event messages for stream " + *logStreamName + " in log group  " + *logGroupName)

    gotToken := ""
    nextToken := ""

    for _, event := range resp.Events {
        gotToken = nextToken
        nextToken = *resp.NextForwardToken

        if gotToken == nextToken {
            break
        }

        fmt.Println("  ", *event.Message)
        // snippet-end:[cloudwatch.go.getlogevents.print]
    }
}
// snippet-end:[cloudwatch.go.getlogevents]
