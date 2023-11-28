// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"encoding/json"
	"log"
	"fmt"
	"os"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
)

// snippet-start:[gov2.bedrock-runtime.InvokeModelWrapper.complete]
// snippet-start:[gov2.bedrock-runtime.InvokeModelWrapper.struct]

// InvokeModelWrapper encapsulates Amazon Bedrock actions used in the examples.
// It contains a Bedrock Runtime client that is used to invoke foundation models.
type InvokeModelWrapper struct {
	BedrockRuntimeClient *bedrockruntime.Client
}

// snippet-end:[gov2.bedrock-runtime.InvokeModelWrapper.struct]

// snippet-start:[gov2.bedrock-runtime.InvokeClaude]

// Each model provider has their own individual request and response formats.
// For the format, ranges, and default values for Anthropic Claude, refer to:
// https://docs.anthropic.com/claude/reference/complete_post

type ClaudeRequest struct {
	Prompt            string   `json:"prompt"`
	MaxTokensToSample int      `json:"max_tokens_to_sample"`
	Temperature       float64  `json:"temperature,omitempty"`
	StopSequences     []string `json:"stop_sequences,omitempty"`
}

type ClaudeResponse struct {
	Completion string `json:"completion"`
}

// Invokes Anthropic Claude on Amazon Bedrock to run an inference using the input
// provided in the request body.
func (wrapper InvokeModelWrapper) InvokeClaude(prompt string) (string, error) {

    modelId := "anthropic.claude-v2"

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
		ModelId: aws.String(modelId),
		ContentType: aws.String("application/json"),
		Body: body,
	})

	if err != nil {
        errMsg := err.Error()
        if strings.Contains(errMsg, "no such host") {
            fmt.Printf("The Bedrock service is not available in the selected region. Please double-check the service availability for your region at https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/.\n")
        } else if strings.Contains(errMsg, "Could not resolve the foundation model") {
            fmt.Printf("Could not resolve the foundation model from model identifier: \"%v\". Please verify that the requested model exists and is accessible within the specified region.\n", modelId)
        } else {
            fmt.Printf("Couldn't invoke Anthropic Claude. Here's why: %v\n", err)
        }
        os.Exit(1)
    }

	var response ClaudeResponse

	err = json.Unmarshal(output.Body, &response)

	if err != nil {
		fmt.Println(err)
		log.Fatal("failed to unmarshal", err)
	}

	return response.Completion, nil
}

// snippet-end:[gov2.bedrock-runtime.InvokeClaude]

// snippet-start:[gov2.bedrock-runtime.InvokeJurassic2]

// Each model provider has their own individual request and response formats.
// For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
// https://docs.ai21.com/reference/j2-complete-ref

type Jurassic2Request struct {
	Prompt            string   `json:"prompt"`
	MaxTokens	 	  int      `json:"maxTokens,omitempty"`
	Temperature       float64  `json:"temperature,omitempty"`
}

type Jurassic2Response struct { Completions []Completion `json:"completions"` }
type Completion struct { Data Data `json:"data"` }
type Data struct { Text string `json:"text"` }


// Invokes AI21 Labs Jurassic-2 on Amazon Bedrock to run an inference using the input
// provided in the request body.
func (wrapper InvokeModelWrapper) InvokeJurassic2(prompt string) (string, error) {

    modelId := "ai21.j2-mid-v1"

	request := Jurassic2Request {
		Prompt:            prompt,
		MaxTokens: 		   200,
		Temperature:       0.5,
	}

	body, err := json.Marshal(request)

	output, err := wrapper.BedrockRuntimeClient.InvokeModel(context.TODO(), &bedrockruntime.InvokeModelInput{
		ModelId: aws.String(modelId),
		ContentType: aws.String("application/json"),
		Body: body,
	})

	if err != nil {
    	errMsg := err.Error()
    	if strings.Contains(errMsg, "no such host") {
    		fmt.Printf("The Bedrock service is not available in the selected region. Please double-check the service availability for your region at https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/.\n")
    	} else if strings.Contains(errMsg, "Could not resolve the foundation model") {
    	    fmt.Printf("Could not resolve the foundation model from model identifier: \"%v\". Please verify that the requested model exists and is accessible within the specified region.\n", modelId)
    	} else {
            fmt.Printf("Couldn't invoke AI21 Labs Jurassic-2. Here's why: %v\n", err)
        }
        os.Exit(1)
    }

	var response Jurassic2Response

	err = json.Unmarshal(output.Body, &response)

	if err != nil {
		log.Fatal("failed to unmarshal", err)
	}

	return response.Completions[0].Data.Text, nil
}

// snippet-end:[gov2.bedrock-runtime.InvokeJurassic2]

// snippet-start:[gov2.bedrock-runtime.InvokeLlama2]

// Each model provider has their own individual request and response formats.
// For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
// https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html

type Llama2Request struct {
	Prompt            string   `json:"prompt"`
	MaxGenLength 	  int      `json:"max_gen_len,omitempty"`
	Temperature       float64  `json:"temperature,omitempty"`
}

type Llama2Response struct {
	Generation string `json:"generation"`
}

// Invokes Meta Llama 2 Chaton Amazon Bedrock to run an inference using the input
// provided in the request body.
func (wrapper InvokeModelWrapper) InvokeLlama2(prompt string) (string, error) {

    modelId := "meta.llama2-13b-chat-v1"

	request := Llama2Request {
		Prompt:            prompt,
		MaxGenLength:	   512,
		Temperature:       0.5,
	}

	body, err := json.Marshal(request)

	output, err := wrapper.BedrockRuntimeClient.InvokeModel(context.TODO(), &bedrockruntime.InvokeModelInput{
		ModelId: aws.String(modelId),
		ContentType: aws.String("application/json"),
		Body: body,
	})

	if err != nil {
        errMsg := err.Error()
        if strings.Contains(errMsg, "no such host") {
            fmt.Printf("The Bedrock service is not available in the selected region. Please double-check the service availability for your region at https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/.\n")
        } else if strings.Contains(errMsg, "Could not resolve the foundation model") {
            fmt.Printf("Could not resolve the foundation model from model identifier: \"%v\". Please verify that the requested model exists and is accessible within the specified region.\n", modelId)
        } else {
            fmt.Printf("Couldn't invoke Meta Llama 2. Here's why: %v\n", err)
        }
        os.Exit(1)
    }

	var response Llama2Response

	err = json.Unmarshal(output.Body, &response)

	if err != nil {
		log.Fatal("failed to unmarshal", err)
	}

	return response.Generation, nil
}

// snippet-end:[gov2.bedrock-runtime.InvokeLlama2]

// snippet-end:[gov2.bedrock-runtime.InvokeModelWrapper.complete]