// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
  ) 

// snippet-start:[gov2.bedrock-runtime.InvokeModelWrapper.complete]
// snippet-start:[gov2.bedrock-runtime.InvokeModelWrapper.struct]

// InvokeModelWrapper encapsulates Amazon Bedrock actions used in the examples.
// It contains a Bedrock service client that is used to perform foundation model actions.
type InvokeModelWrapper struct {
	BedrockRuntimeClient *bedrockruntime.Client
}

// snippet-end:[gov2.bedrock-runtime.InvokeModelWrapper.struct]

// snippet-start:[gov2.bedrock-runtime.InvokeClaude]

// Each model provider has their own individual request and response formats.
// For the format, ranges, and default values for Anthropic Claude, refer to:
// https://docs.anthropic.com/claude/reference/complete_post

type ClaudeRequest struct {
	// Required request parameters
	Prompt            string   `json:"prompt"`
	MaxTokensToSample int      `json:"max_tokens_to_sample"`
	// Optional request parameters
	Temperature       float64  `json:"temperature,omitempty"`
	TopP              float64  `json:"top_p,omitempty"`
	TopK              int      `json:"top_k,omitempty"`
	StopSequences     []string `json:"stop_sequences,omitempty"`
}

type ClaudeResponse struct {
	Completion string `json:"completion"`
}

// Invokes Anthropic Claude on Amazon Bedrock to run an inference using the input
// provided in the request body.
func (wrapper InvokeModelWrapper) InvokeClaude(prompt string) (string, error) {

	// Anthropic Claude requires you to enclose the prompt as follows:
	prefix := "Human: "
	postfix := "\n\nAssistant:"
	prompt = prefix + prompt + postfix

	request := ClaudeRequest {
		Prompt:            prompt,
		MaxTokensToSample: 200,
		Temperature:       0.5,
		StopSequences:     []string{"\n\nHuman:"},
	}

	body, err := json.Marshal(request)

	result, err := wrapper.BedrockRuntimeClient.InvokeModel(context.TODO(), &bedrockruntime.InvokeModelInput{
		ModelId: aws.String("anthropic.claude-v2"),
		ContentType: aws.String("application/json"),
		Body: body,
	})

	if err != nil {
		log.Printf("Couldn't invoke Claude. Here's why: %v\n", err)
	}

	var response ClaudeResponse

	err = json.Unmarshal(result.Body, &response)

	if err != nil {
		log.Fatal("failed to unmarshal", err)
	}

	return response.Completion, nil
}

// snippet-end:[gov2.bedrock-runtime.InvokeClaude]

// snippet-end:[gov2.bedrock-runtime.InvokeModelWrapper.complete]