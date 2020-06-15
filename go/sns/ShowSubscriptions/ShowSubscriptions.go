// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[sns.go.show_subscriptions]
package main

// snippet-start:[sns.go.show_subscriptions.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sns"
    "github.com/aws/aws-sdk-go/service/sns/snsiface"
)
// snippet-end:[sns.go.show_subscriptions.imports]

// GetSubscriptions retrieves a list of your Amazon SNS subscriptions
// Inputs:
//     svc is an Amazon SNS service client
// Output:
//     If success, information about your subscriptions and nil
//     Otherwise, nil and an error from the call to ListSubscriptions
func GetSubscriptions(svc snsiface.SNSAPI) (*sns.ListSubscriptionsOutput, error) {
    // snippet-start:[sns.go.show_subscriptions.call]
    result, err := svc.ListSubscriptions(nil)
    // snippet-end:[sns.go.show_subscriptions.call]

    return result, err
}

func main() {
    // snippet-start:[sns.go.show_subscriptions.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sns.New(sess)
    // snippet-end:[sns.go.show_subscriptions.session]

    result, err := GetSubscriptions(svc)
    if err != nil {
        fmt.Println("Got an error retrieving the subscriptions:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sns.go.show_subscriptions.display]
    fmt.Println("Topic ARN")
    fmt.Println("Subscription ARN")
    fmt.Println("-------------------------")
    for _, s := range result.Subscriptions {
        fmt.Println(*s.TopicArn)
        fmt.Println(*s.SubscriptionArn)

        fmt.Println("")
    }
    // snippet-end:[sns.go.show_subscriptions.display]
}
// snippet-end:[sns.go.show_subscriptions]
