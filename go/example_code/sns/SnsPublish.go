// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[SnsPublish.go -m MESSAGE -t TOPIC-ARN sends MESSAGE to all of the users subscribed to the SNS topic with the ARN TOPIC-ARN.]
// snippet-keyword:[Amazon Simple Notification Service]
// snippet-keyword:[Amazon SNS]
// snippet-keyword:[Publish function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[sns]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-25]
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
// snippet-start:[sns.go.publish]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sns"

    "flag"
    "fmt"
    "os"
)

func main() {
    msgPtr := flag.String("m", "", "The message to send to the subscribed users of the topic")
    topicPtr := flag.String("t", "", "The ARN of the topic to which the user subscribes")
    flag.Parse()
    message := *msgPtr
    topicArn := *topicPtr

    if message == "" || topicArn == "" {
        fmt.Println("You must supply a message and topic ARN")
        fmt.Println("Usage: go run SnsPublish.go -m MESSAGE -t TOPIC-ARN")
        os.Exit(1)
    }

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sns.New(sess)

    result, err := svc.Publish(&sns.PublishInput{
        Message:  aws.String(message),
        TopicArn: topicPtr,
    })
    if err != nil {
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println(*result.MessageId)
}
// snippet-end:[sns.go.publish]
