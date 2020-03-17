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
// snippet-start:[sqs.go.list_queues]
package main

// snippet-start:[sqs.go.list_queues.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"
)
// snippet-end:[sqs.go.list_queues.imports]

// GetQueues returns a list of queue names
func GetQueues(sess *session.Session) (*sqs.ListQueuesOutput, error) {
    // Create an SQS service client
    // snippet-start:[sqs.go.list_queues.call]
    svc := sqs.New(sess)

    result, err := svc.ListQueues(nil)
    // snippet-end:[sqs.go.list_queues.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // Create a session that gets credential values from ~/.aws/credentials
    // and the default region from ~/.aws/config
    // snippet-start:[sqs.go.list_queues.sess]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.list_queues.sess]

    result, err := GetQueues(sess)
    if err != nil {
        fmt.Println("Got an error retrieving queue URLs:")
        fmt.Println(err)
        return
    }

    // snippet-start:[sqs.go.list_queues.display]
    for i, url := range result.QueueUrls {
        fmt.Printf("%d: %s\n", i, *url)
    }
    // snippet-end:[sqs.go.list_queues.display]
}
// snippet-end:[sqs.go.list_queues]
