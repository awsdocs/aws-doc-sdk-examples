// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

// snippet-start:[gov2.bedrock-runtime.Scenario_Converse]

import (
	"context"
	"log"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// ConverseScenario demonstrates how to use the Amazon Bedrock Runtime client
// to Converse with Anthropic Claude
type ConverseScenario struct {
	sdkConfig       aws.Config
	questioner      demotools.IQuestioner
	converseWrapper actions.ConverseWrapper
}

// NewConverseScenario constructs a ConverseScenario instance from a configuration.
// It uses the specified config to get a Bedrock Runtime client and create wrappers for the
// actions used in the scenario.
func NewConverseScenario(sdkConfig aws.Config, questioner demotools.IQuestioner) ConverseScenario {
	client := bedrockruntime.NewFromConfig(sdkConfig)
	return ConverseScenario{
		sdkConfig:       sdkConfig,
		questioner:      questioner,
		converseWrapper: actions.ConverseWrapper{BedrockRuntimeClient: client},
	}
}

// Run runs the interactive scenario.
func (scenario ConverseScenario) Run(ctx context.Context) {
	defer func() {
		if r := recover(); r != nil {
			log.Printf("Something went wrong with the demo: %v\n", r)
		}
	}()

	log.Println(strings.Repeat("=", 77))
	log.Println("Welcome to the Amazon Bedrock Runtime model invocation demo.")
	log.Println(strings.Repeat("=", 77))

	log.Printf("First, let's invoke a few large-language models using the synchronous client:\n\n")

	text2textPrompt := "In one paragraph, who are you?"

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Claude with prompt: %v\n", text2textPrompt)
	scenario.ConverseClaude(ctx, text2textPrompt)

	log.Println(strings.Repeat("=", 77))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("=", 77))
}

func (scenario ConverseScenario) ConverseClaude(ctx context.Context, prompt string) {
	completion, err := scenario.converseWrapper.ConverseClaude(ctx, prompt)
	if err != nil {
		panic(err)
	}
	log.Printf("\nClaude     : %v\n", strings.TrimSpace(completion))
}

// snippet-end:[gov2.bedrock-runtime.Scenario_Converse]
