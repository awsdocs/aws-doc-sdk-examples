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

// snippet-start:[gov2.bedrock.InvokeModelWrapper.complete]
// snippet-start:[gov2.bedrock.InvokeModelWrapper.struct]

// InvokeModelWrapper encapsulates Amazon Bedrock actions used in the examples.
// It contains a Bedrock service client that is used to perform foundation model actions.
type InvokeModelWrapper struct {
	BedrockRuntimeClient *bedrockruntime.Client
}

// snippet-end:[gov2.bedrock.InvokeModelWrapper.struct]

// snippet-start:[gov2.bedrock.InvokeClaude]

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

// Invokes Anthropic Claude on Amazon Bedrock to run an inference using the input
// provided in the request body.
func (wrapper InvokeModelWrapper) InvokeClaude(prompt string) ([]byte, error) {
	var completion []byte

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

	output, err := wrapper.BedrockRuntimeClient.InvokeModel(context.TODO(), &bedrockruntime.InvokeModelInput{
		ModelId: aws.String("anthropic.claude-v2"),
		Body: body,
	})

	if err != nil {
		log.Printf("Couldn't invoke Claude. Here's why: %v\n", err)
	} else {
		completion = output.Body
	}
	return completion, err
}



// Invoke takes a struct and returns a string and error
// func Invoke(input InvokeModelWrapper) (string, error) {
//   if input == nil {
//     return "", errors.New("input is required")
//   }
  
//   output, err := generateOutput(input)
//   if err != nil {
//     return "", err
//   }
  
//   return output, nil
// }

// // generateOutput takes input and returns output string and error
// func generateOutput(input InvokeModelWrapper) (string, error) {
//   // logic to generate output
// }

// // documentation

