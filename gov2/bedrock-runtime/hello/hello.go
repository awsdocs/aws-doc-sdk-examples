// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.bedrock-runtime.Hello]

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

// Each model provider defines their own individual request and response formats.
// For the format, ranges, and default values for the different models, refer to:
// https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters.html

type ClaudeRequest struct {
	Prompt            string   `json:"prompt"`
	MaxTokensToSample int      `json:"max_tokens_to_sample"`
	// Omitting optional request parameters
}

type ClaudeResponse struct {
	Completion string `json:"completion"`
}

// main uses the AWS SDK for Go (v2) to create an Amazon Bedrock Runtime client
// and invokes Anthropic Claude 2 inside your account and the chosen region.
// This example uses the default settings specified in your shared credentials
// and config files.
func main() {

	region := "us-east-1"
	sdkConfig, err := config.LoadDefaultConfig(context.Background(), config.WithRegion(region))
    if err != nil {
        fmt.Println("Couldn't load default configuration. Have you set up your AWS account?")
        fmt.Println(err)
        return
    }

	client := bedrockruntime.NewFromConfig(sdkConfig)

	prompt := "Hello, how are you today?"
	fmt.Println("Prompt:\n", prompt)

	// Anthropic Claude requires you to enclose the prompt as follows:
	prefix := "Human: "
	postfix := "\n\nAssistant:"
	prompt = prefix + prompt + postfix

	request := ClaudeRequest {
		Prompt:            prompt,
		MaxTokensToSample: 200,
	}

	body, err := json.Marshal(request)

	result, err := client.InvokeModel(context.Background(), &bedrockruntime.InvokeModelInput {
		ModelId:     aws.String("anthropic.claude-v2"),
		ContentType: aws.String("application/json"),
		Body:        body,
	})

	if err != nil {
		log.Printf("Couldn't invoke Anthropic Claude. Here's why: %v\n", err)
	}

	var response ClaudeResponse

	err = json.Unmarshal(result.Body, &response)

	if err != nil {
		log.Fatal("failed to unmarshal", err)
	}

	fmt.Println("Response from Anthropic Claude:\n", response.Completion)
}

// snippet-end:[gov2.bedrock-runtime.Hello]