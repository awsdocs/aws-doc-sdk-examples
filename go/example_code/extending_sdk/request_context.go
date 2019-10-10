// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[request_context.go shows how to use context.Context with SDK requests.]
// snippet-keyword:[Extending the SDK]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[aws-go-sdk]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-03-14]
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
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/sqs"

    "context"
    "fmt"
    "time"
)

func main() {
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := sqs.New(sess)

    // URL to our queue
    qURL := "QueueURL"

    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    // SQS ReceiveMessage
    params := &sqs.ReceiveMessageInput{
        AttributeNames: []*string{
            aws.String(sqs.MessageSystemAttributeNameSentTimestamp),
        },
        MessageAttributeNames: []*string{
            aws.String(sqs.QueueAttributeNameAll),
        },
        QueueUrl:            &qURL,
        MaxNumberOfMessages: aws.Int64(1),
        VisibilityTimeout:   aws.Int64(20), // 20 seconds
        WaitTimeSeconds:     aws.Int64(0),
    }

    //snippet-start:[extending.go.receive_message_request]
    req, resp := svc.ReceiveMessageRequest(params)
    req.HTTPRequest = req.HTTPRequest.WithContext(ctx)
    
    err := req.Send()
    if err != nil {
        fmt.Println("Got error receiving message:")
        fmt.Println(err.Error())
    } else {
        fmt.Println(resp)
    }
    //snippet-end:[extending.go.receive_message_request]
}
