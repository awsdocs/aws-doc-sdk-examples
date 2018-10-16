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
    "github.com/aws/aws-sdk-go/service/cloudtrail"

    "flag"
    "fmt"
    "os"
    "time"
)

func main() {
    // Trail name required
    var trailName string
    flag.StringVar(&trailname, "n", "", "The name of the trail")

    // Option to show event
    var showEvent bool
    flag.BoolVar (&showEvent, "s", false, "Whether to show the event")

    flag.Parse()

    if trailName == "" {
        fmt.Println("You must supply a trail name")
        os.Exit(1)
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create CloudTrail client
    svc := cloudtrail.New(sess)

    input := &cloudtrail.LookupEventsInput{EndTime: aws.Time(time.Now())}

    resp, err := svc.LookupEvents(input)
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Found", len(resp.Events),"events before now")
    fmt.Println("")

    for _, event := range resp.Events {
        if showEvents {
            fmt.Println("Event:")
            fmt.Println(aws.StringValue(event.CloudTrailEvent))
            fmt.Println("")
        }

        fmt.Println("Name    ", aws.StringValue(event.EventName))
        fmt.Println("ID:     ", aws.StringValue(event.EventId))
        fmt.Println("Time:   ", aws.TimeValue(event.EventTime))
        fmt.Println("User:   ", aws.StringValue(event.Username))

        fmt.Println("Resourcs:")

        for _, resource := range event.Resources {
            fmt.Println("  Name:", aws.StringValue(resource.ResourceName))
            fmt.Println("  Type:", aws.StringValue(resource.ResourceType))
        }

        fmt.Println("")
    }
}
// Tags for sample catalog:

//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Lists the AWS CloudTrail events.]
//snippet-keyword:[AWS CloudTrail]
//snippet-keyword:[LookupEvents function]
//snippet-keyword:[Go]
//snippet-service:[cloudtrail]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]