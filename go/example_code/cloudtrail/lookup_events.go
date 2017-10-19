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
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
    "time"
)

func main() {
    // Trail name required
    trailNamePtr := flag.String("n", "", "The name of the trail to delete")
    // Optional region
    regionPtr := flag.String("r", "us-west-2", "The region for the trail.")

    // Option to show event
    showEventsPtr := flag.Bool("s", false, "Whether to show the event")

    flag.Parse()

    regionName := *regionPtr
    trailName := *trailNamePtr
    showEvents := *showEventsPtr

    if trailName == "" {
        fmt.Println("You must supply a trail name")
        os.Exit(1)
    }

    // Initialize a session in us-west-2 that the SDK will use to load configuration,
    // and credentials from the shared config file ~/.aws/config.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String(regionName)},
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
		fmt.Println("Source: ", aws.StringValue(event.EventSource))
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
