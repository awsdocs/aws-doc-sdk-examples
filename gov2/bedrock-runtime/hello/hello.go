// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.bedrockruntime.Hello]

package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
)

const region = "us-east-1"

// Each model provider has their own individual request and response formats.
// For the format, ranges, and default values for Anthropic Claude, refer to:
// https://docs.anthropic.com/claude/reference/complete_post

type ClaudeRequest struct {
	Prompt            string   `json:"prompt"`
	MaxTokensToSample int      `json:"max_tokens_to_sample"`
	// Omitting optional request parameters
}

// main uses the AWS SDK for Go (v2) to create an Amazon Bedrock Runtimeclient 
// and list the available foundation models in your account and the chosen region.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.Background(), config.WithRegion(region))
    if err != nil {
        fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
        fmt.Println(err)
        return
    }
	fmt.Println(sdkConfig.Credentials.Retrieve(context.Background()))

	client := bedrockruntime.NewFromConfig(sdkConfig)

	// Anthropic Claude requires you to enclose the prompt as follows:
	prefix := "Human: "
	postfix := "\n\nAssistant:"
	prompt := prefix + "Hello, how are you today?" + postfix

	request := ClaudeRequest {
		Prompt:            prompt,
		MaxTokensToSample: 200,
	}

	body, err := json.Marshal(request)

	result, err := client.InvokeModel(context.Background(), &bedrockruntime.InvokeModelInput {
		ModelId: aws.String("anthropic.claude-v2"),
		Body: body,
	})

	if err != nil {
		log.Printf("Couldn't invoke Claude. Here's why: %v\n", err)
	} else {
		completion := result.Body
		fmt.Println(completion)
	}
}

// snippet-end:[gov2.bedrockruntime.Hello]