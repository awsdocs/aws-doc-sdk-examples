// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[SnsCreateTopic.go TOPIC creates the SNS topic TOPIC.]
// snippet-keyword:[Amazon Simple Notification Service]
// snippet-keyword:[Amazon SNS]
// snippet-keyword:[CreateTopic function]
// snippet-keyword:[Go]
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
// snippet-start:[sns.go.create_topic]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sns"

    "fmt"
    "os"
)

func main() {
    if len(os.Args) < 2 {
        fmt.Println("You must supply a topic name")
        fmt.Println("Usage: go run SnsCreateTopic.go TOPIC")
        os.Exit(1)
    }

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sns.New(sess)

    result, err := svc.CreateTopic(&sns.CreateTopicInput{
        Name: aws.String(os.Args[1]),
    })
    if err != nil {
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println(*result.TopicArn)
}
// snippet-end:[sns.go.create_topic]
