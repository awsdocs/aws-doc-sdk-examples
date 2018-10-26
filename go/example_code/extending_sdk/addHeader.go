//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Adds a custom header to a DynamoDB table.]
//snippet-keyword:[Amazon DynamoDB]
//snippet-keyword:[Handlers.Send.PushFront function]
//snippet-keyword:[ListTables function]
//snippet-keyword:[Go]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
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
    "github.com/aws/aws-sdk-go/aws/request"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/dynamodb"

    "fmt"
    "os"
)

func main() {
    // Initialize a session in us-west-2 that the SDK will use to load credentials
    // from the shared config file. (~/.aws/credentials).
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )
    if err != nil {
        fmt.Println("Error getting session:")
        fmt.Println(err)
        os.Exit(1)
    }

    // Create DynamoDB client
    // and expose HTTP requests/responses
    svc := dynamodb.New(sess, aws.NewConfig().WithLogLevel(aws.LogDebugWithHTTPBody))

    // Add "CustomHeader" header with value of 10
    svc.Handlers.Send.PushFront(func(r *request.Request) {
        r.HTTPRequest.Header.Set("CustomHeader", fmt.Sprintf("%d", 10))
    })

    // Call ListTables just to see HTTP request/response
    // The request should have the CustomHeader set to 10
    _, _ = svc.ListTables(&dynamodb.ListTablesInput{})
}
