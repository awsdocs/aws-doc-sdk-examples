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
