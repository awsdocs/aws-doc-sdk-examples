// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[cloudwatch.go.lambda_main]
package main

import (
    "context"
    "fmt"

    "github.com/aws/aws-lambda-go/events"
    "github.com/aws/aws-lambda-go/lambda"
)

// HandleRequest logs the CloudWatch logs events
func HandleRequest(ctx context.Context, event events.CloudWatchEvent) {
    fmt.Println("LogEC2InstanceStateChange:")
    fmt.Print(event)
}

func main() {
    lambda.Start(HandleRequest)
}
// snippet-end:[cloudwatch.go.lambda_main]
