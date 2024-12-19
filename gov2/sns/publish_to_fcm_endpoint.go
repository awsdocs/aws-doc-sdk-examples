package main

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sns"
)

// This code example shows you how to send a push notification to a Firebase Cloud Messaging (FCM) endpoint
// using AWS SNS SDK.

func main() {
	ctx := context.Background()

	sdkConfig, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}

	snsClient := sns.NewFromConfig(sdkConfig)

	notificationData := map[string]interface{}{
		"notification": map[string]string{
			"title": "Notification Title",
			"body":  "Sample Body",
		},
		"data": map[string]string{
			"sampleData1": "Sample Data",
			"sampleData2": "Sample Data",
		},
	}

	// We need to Marshal this data into JSON format, then put it as a
	// value to GCM key inside another map, and then marshal that
	// map to get the required format to be sent to SNS.

	notificationDataJSON, err := json.Marshal(notificationData)

	if err != nil {
		fmt.Println(err)
		return
	}

	message := map[string]interface{}{
		"default": "Default Value",
		"GCM": string(notificationDataJSON),
	}

	messageJSON, err := json.Marshal(message)

	if err != nil {
		fmt.Println(err)
		return
	}

	endpointArn := "user-endpoint-arn"

	_, err = snsClient.Publish(ctx, &sns.PublishInput{
		TargetArn:        &endpointArn,
		MessageStructure: aws.String("json"),
		Message:          aws.String(string(messageJSON)),
	})

	if err != nil {
		fmt.Println(err)
		return
	}

	// or if you want to pass the message data directly as a string to avoid the double
	// JSON marshalling, you can do that by passing it in the following format:
	//
	// Message: aws.String(
	// "{\"default\":\"Default Value\","+
	//   "\"GCM\":\"{\\\"notification\\\":{" +
	// 		"\\\"body\\\":\\\"Body\\\"," +
	// 		"\\\"title\\\":\\\"Title\\\"" +
	// 		"}," +
	// 		"\\\"data\\\":{" +
	// 		"\\\"sampleData1\\\":\\\"Sample Data\\\"," +
	// 		"\\\"sampleData2\\\":\\\"Sample Data\\\"" +
	// 		"}}\"}"
	// ),
}
