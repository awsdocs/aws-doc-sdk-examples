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

package main

import (
    "encoding/json"
    "fmt"
    "io/ioutil"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchevents"
)

// Event represents the information for a new event
type Event struct {
    Details []struct {
        Key   string `json:"Key"`
        Value string `json:"Value"`
    } `json:"Details"`
    DetailType  string `json:"DetailType"`
    ResourceArn string `json:"ResourceArn"`
    Source      string `json:"Source"`
}

var eventFile = "event.json"

func getEventInfo(fileName string) (Event, error) {
    var e Event

    content, err := ioutil.ReadFile(fileName)
    if err != nil {
        return e, err
    }

    // Convert []byte to string
    text := string(content)

    // Marshall JSON string in text into global struct
    err = json.Unmarshal([]byte(text), &e)
    if err != nil {
        return e, err
    }

    return e, nil
}

// CreateEvent creates an event
func CreateEvent(sess *session.Session, event Event) error {
    // Create the cloudwatch events client
    svc := cloudwatchevents.New(sess)

    myDetails := "{ "
    for _, d := range event.Details {
        myDetails = myDetails + "\"" + d.Key + "\": \"" + d.Value + "\","
    }

    myDetails = myDetails + " }"

    _, err := svc.PutEvents(&cloudwatchevents.PutEventsInput{
        Entries: []*cloudwatchevents.PutEventsRequestEntry{
            &cloudwatchevents.PutEventsRequestEntry{
                Detail:     aws.String(myDetails),        // "{ \"key1\": \"value1\", \"key2\": \"value2\" }"),
                DetailType: aws.String(event.DetailType), // "appRequestSubmitted"),
                Resources: []*string{
                    aws.String(event.ResourceArn), // "RESOURCE_ARN"),
                },
                Source: aws.String(event.Source), // "com.company.myapp"),
            },
        },
    })
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    event, err := getEventInfo(eventFile)
    if err != nil {
        fmt.Println("Could not get event info from " + eventFile)
        return
    }

    err = CreateEvent(sess, event)
    if err != nil {
        fmt.Println("Could not create event")
        return
    }

    fmt.Println("Created event")
}
