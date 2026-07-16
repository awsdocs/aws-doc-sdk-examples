// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.bedrock-runtime.Hello]

package main

import (
	"context"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime/types"
)

// main uses the AWS SDK for Go (v2) to create an Amazon Bedrock Runtime client
// and invokes Anthropic Claude using the Converse API.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {

	region := flag.String("region", "us-east-1", "The AWS region")
	flag.Parse()

	fmt.Printf("Using AWS region: %s\n", *region)

	ctx := context.Background()
	sdkConfig, err := config.LoadDefaultConfig(ctx, config.WithRegion(*region))
	if err != nil {
		fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
		fmt.Println(err)
		return
	}

	client := bedrockruntime.NewFromConfig(sdkConfig)

	// Set the model ID, e.g., Claude 3 Haiku.
	modelId := "anthropic.claude-3-haiku-20240307-v1:0"

	// Start a conversation with the user message.
	prompt := "Hello. In a short paragraph, explain what you can do."

	message := types.Message{
		Content: []types.ContentBlock{
			&types.ContentBlockMemberText{Value: prompt},
		},
		Role: types.ConversationRoleUser,
	}

	fmt.Printf("Model: %s\n", modelId)
	fmt.Printf("Prompt: %s\n\n", prompt)

	result, err := client.Converse(ctx, &bedrockruntime.ConverseInput{
		ModelId:  aws.String(modelId),
		Messages: []types.Message{message},
	})

	if err != nil {
		log.Fatalf("ERROR: Can't invoke '%s'. Reason: %v\n", modelId, err)
	}

	// Extract and print the response text.
	responseMessage, _ := result.Output.(*types.ConverseOutputMemberMessage)
	responseContentBlock := responseMessage.Value.Content[0]
	text, _ := responseContentBlock.(*types.ContentBlockMemberText)
	fmt.Printf("Response: %s\n", text.Value)
}

// snippet-end:[gov2.bedrock-runtime.Hello]
