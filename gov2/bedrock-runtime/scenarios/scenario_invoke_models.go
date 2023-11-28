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

// snippet-start:[gov2.bedrock-runtime.Scenario_InvokeModels]

// InvokeModelsScenario demonstrates how to use the Amazon Bedrock Runtime client
// to invoke various foundation models for text and image generation
//
// 1. Generate text with Anthropic Claude 2
// 2. Generate text with AI21 Labs Jurassic-2
// 3. Generate text with Meta Llama 2 Chat
// 4. Generate text and asynchronously process the response stream with Anthropic Claude 2
type InvokeModelsScenario struct {
	sdkConfig aws.Config
	invokeModelWrapper actions.InvokeModelWrapper
	responseStreamWrapper actions.InvokeModelWithResponseStreamWrapper
	questioner demotools.IQuestioner
	isTestRun bool
}

// NewInvokeModelsScenario constructs an InvokeModelsScenario instance from a configuration.
// It uses the specified config to get a Bedrock Runtime client and create wrappers for the
// actions used in the scenario.
func NewInvokeModelsScenario(sdkConfig aws.Config, questioner demotools.IQuestioner) InvokeModelsScenario {
	client := bedrockruntime.NewFromConfig(sdkConfig)
	return InvokeModelsScenario{
		sdkConfig:		 	   sdkConfig,
		invokeModelWrapper:    actions.InvokeModelWrapper{client},
		responseStreamWrapper: actions.InvokeModelWithResponseStreamWrapper{client},
		questioner:            questioner,
	}
}

// Run runs the interactive scenario.
func (scenario InvokeModelsScenario) Run() {
	defer func() {
        if r := recover(); r != nil {
            log.Printf("Something went wrong with the demo: %v\n", r)
        }
    }()

	prompt := "In one paragraph, who are you?"

	log.Println(strings.Repeat("=", 77))
	log.Println("Welcome to the Amazon Bedrock Runtime model invocation demo.")
	log.Println(strings.Repeat("=", 77))

	log.Printf("First, let's invoke a few large-language models using the synchronous client:\n\n")

    log.Println(strings.Repeat("-", 77))
    log.Printf("Invoking Claude with prompt: %v\n", prompt)
	scenario.InvokeClaude(prompt)

    log.Println(strings.Repeat("-", 77))
    log.Printf("Invoking Jurassic-2 with prompt: %v\n", prompt)
	scenario.InvokeJurassic2(prompt)

    log.Println(strings.Repeat("-", 77))
    log.Printf("Invoking Llama2 with prompt: %v\n", prompt)
	scenario.InvokeLlama2(prompt)

	log.Println(strings.Repeat("=", 77))
	log.Printf("Now, let's invoke Claude with the asynchronous client and process the response stream:\n\n")

    log.Println(strings.Repeat("-", 77))
    log.Printf("Invoking Claude with prompt: %v\n", prompt)
	scenario.InvokeWithResponseStream(prompt)

	log.Println(strings.Repeat("=", 77))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("=", 77))
}

func (scenario InvokeModelsScenario) InvokeClaude(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeClaude(prompt)
	if err != nil { panic(err) }
	log.Printf("\nClaude     : %v\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeJurassic2(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeJurassic2(prompt)
	if err != nil { panic(err) }
	log.Printf("\nJurassic-2 : %v\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeLlama2(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeLlama2(prompt)
	if err != nil { panic(err) }
	log.Printf("\nLlama 2    : %v\n\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeWithResponseStream(prompt string) {
	log.Println("\nClaude with response stream:\n")
	_, err := scenario.responseStreamWrapper.InvokeModelWithResponseStream(prompt)
	if err != nil { panic(err) }
	log.Println()
}

// snippet-end:[gov2.bedrock-runtime.Scenario_InvokeModels]