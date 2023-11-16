// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"log"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/actions"
)

// snippet-start:[gov2.bedrock-runtime.InvokeModels]

// InvokeModelsScenario demosytrates how to use the Amazon Bedrock Runtime client
// to invoke various foundation models for text and image generation
//
// 1. Generate text with Anthropic Claude 2
// 2. Generate text with AI21 Labs Jurassic-2
// 3. Generate text with Meta Llama 2 Chat
// 4. Generate an image with Stability.ai Stable Diffusion
type InvokeModelsScenario struct {
	sdkConfig aws.Config
	wrapper actions.InvokeModelWrapper
	questioner demotools.IQuestioner
	isTestRun bool
}

// NewInvokeModelsScenario constructs an InvokeModelsScenarion instance from a configuration.
// It uses the specified config to get a Bedrock Runtime client and create a wrapper for the
// actions used in the scenario.
func NewInvokeModelsScenario(sdkConfig aws.Config, questioner demotools.IQuestioner) InvokeModelsScenario {
	client := bedrockruntime.NewFromConfig(sdkConfig)
	return InvokeModelsScenario{
		sdkConfig: 	sdkConfig,
		wrapper: 	actions.InvokeModelWrapper{client},
		questioner: questioner,
	}
}

// Run runs the interactive scenario.
func (scenario InvokeModelsScenario) Run() {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong with the demo.\n")
			log.Println(r)
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon Bedrock Runtime model invocation demo.")
	log.Println(strings.Repeat("-", 88))

	prompt := "In one sentence, who are you?"

	log.Printf("First, let's invoke a few large-language models:\n\n")
	log.Printf("Prompt: %v\n", prompt)
	log.Println(strings.Repeat("-", 37))

	scenario.InvokeClaude(prompt)

	scenario.InvokeJurassic2(prompt)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

func (scenario InvokeModelsScenario) InvokeClaude(prompt string) {
	completion, err := scenario.wrapper.InvokeClaude(prompt)
	if err != nil { panic(err) }
	log.Printf("\nClaude     : %v\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeJurassic2(prompt string) {
	completion, err := scenario.wrapper.InvokeJurassic2(prompt)
	if err != nil { panic(err) }
	log.Printf("\nJurassic-2 : %v\n\n", strings.TrimSpace(completion))
}