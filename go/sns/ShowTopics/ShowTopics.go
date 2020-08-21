// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[sns.go.show_topics]
package main

// snippet-start:[sns.go.show_topics.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)
// snippet-end:[sns.go.show_topics.imports]

// ShowTopics retrieves information about the Amazon SNS topics
// Inputs:
//     svc is an Amazon SNS service client
// Output:
//     If success, information about the Amazon SNS topics and nil
//     Otherwise, nil and an error from the call to ListTopics
func ShowTopics(svc snsiface.SNSAPI) (*sns.ListTopicsOutput, error) {
    // snippet-start:[sns.go.show_topics.call]
    results, err := svc.ListTopics(nil)
    // snippet-end:[sns.go.show_topics.call]

    return results, err
}

func main() {
    // snippet-start:[sns.go.show_topics.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sns.New(sess)
    // snippet-end:[sns.go.show_topics.session]

    results, err := ShowTopics(svc)
    if err != nil {
        fmt.Println("Got an error retrieving information about the SNS topics:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sns.go.show_topics.display]
    for _, t := range results.Topics {
        fmt.Println(*t.TopicArn)
    }
    // snippet-end:[sns.go.show_topics.display]
}
// snippet-end:[sns.go.show_topics]
