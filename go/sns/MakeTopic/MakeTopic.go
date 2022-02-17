// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[sns.go.make_topic]
package main

// snippet-start:[sns.go.make_topic.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)
// snippet-end:[sns.go.make_topic.imports]

// MakeTopic creates an Amazon SNS topic
// Inputs:
//     svc is an Amazon SNS service client
//     topic is the name of the topic
// Output:
//     If success, information about the topics and nil
//     Otherwise, nil and an error from the call to CreateTopic
func MakeTopic(svc snsiface.SNSAPI, topic *string) (*sns.CreateTopicOutput, error) {
    // snippet-start:[sns.go.make_topic.call]
    results, err := svc.CreateTopic(&sns.CreateTopicInput{
        Name: topic,
    })
    // snippet-end:[sns.go.make_topic.call]

    return results, err
}

func main() {
    // snippet-start:[sns.go.make_topic.args]
    topic := flag.String("t", "", "The name of the topic")
    flag.Parse()

    if *topic == "" {
        fmt.Println("You must supply the name of the topic")
        fmt.Println("-t TOPIC")
        return
    }
    // snippet-end:[sns.go.make_topic.args]

    // snippet-start:[sns.go.make_topic.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sns.New(sess)
    // snippet-end:[sns.go.make_topic.session]

    results, err := MakeTopic(svc, topic)
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    // snippet-start:[sns.go.make_topic.display]
    fmt.Println(*results.TopicArn)
    // snippet-end:[sns.go.make_topic.display]
}
// snippet-end:[sns.go.make_topic]
