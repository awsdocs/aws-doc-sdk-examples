// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"encoding/base64"
	"fmt"
	"log"
	"math/rand"
	"os"
	"path/filepath"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// snippet-start:[gov2.bedrock-runtime.Scenario_InvokeModels]

// InvokeModelsScenario demonstrates how to use the Amazon Bedrock Runtime client
// to invoke various foundation models for text and image generation
//
// 1. Generate text with Anthropic Claude 2
// 2. Generate text with AI21 Labs Jurassic-2
// 3. Generate text with Meta Llama 2 Chat
// 4. Generate text and asynchronously process the response stream with Anthropic Claude 2
// 5. Generate and image with the Amazon Titan image generation model
// 6. Generate text with Amazon Titan Text G1 Express model
type InvokeModelsScenario struct {
	sdkConfig             aws.Config
	invokeModelWrapper    actions.InvokeModelWrapper
	responseStreamWrapper actions.InvokeModelWithResponseStreamWrapper
	questioner            demotools.IQuestioner
}

// NewInvokeModelsScenario constructs an InvokeModelsScenario instance from a configuration.
// It uses the specified config to get a Bedrock Runtime client and create wrappers for the
// actions used in the scenario.
func NewInvokeModelsScenario(sdkConfig aws.Config, questioner demotools.IQuestioner) InvokeModelsScenario {
	client := bedrockruntime.NewFromConfig(sdkConfig)
	return InvokeModelsScenario{
		sdkConfig:             sdkConfig,
		invokeModelWrapper:    actions.InvokeModelWrapper{BedrockRuntimeClient: client},
		responseStreamWrapper: actions.InvokeModelWithResponseStreamWrapper{BedrockRuntimeClient: client},
		questioner:            questioner,
	}
}

// Runs the interactive scenario.
func (scenario InvokeModelsScenario) Run() {
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
	scenario.InvokeClaude(text2textPrompt)

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Jurassic-2 with prompt: %v\n", text2textPrompt)
	scenario.InvokeJurassic2(text2textPrompt)

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Llama2 with prompt: %v\n", text2textPrompt)
	scenario.InvokeLlama2(text2textPrompt)

	log.Println(strings.Repeat("=", 77))
	log.Printf("Now, let's invoke Claude with the asynchronous client and process the response stream:\n\n")

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Claude with prompt: %v\n", text2textPrompt)
	scenario.InvokeWithResponseStream(text2textPrompt)

	log.Println(strings.Repeat("=", 77))
	log.Printf("Now, let's create an image with the Amazon Titan image generation model:\n\n")

	text2ImagePrompt := "stylized picture of a cute old steampunk robot"
	seed := rand.Int63n(2147483648)

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Amazon Titan with prompt: %v\n", text2ImagePrompt)
	scenario.InvokeTitanImage(text2ImagePrompt, seed)

	log.Println(strings.Repeat("-", 77))
	log.Printf("Invoking Titan Text Express with prompt: %v\n", text2textPrompt)
	scenario.InvokeTitanText(text2textPrompt)

	log.Println(strings.Repeat("=", 77))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("=", 77))
}

func (scenario InvokeModelsScenario) InvokeClaude(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeClaude(prompt)
	if err != nil {
		panic(err)
	}
	log.Printf("\nClaude     : %v\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeJurassic2(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeJurassic2(prompt)
	if err != nil {
		panic(err)
	}
	log.Printf("\nJurassic-2 : %v\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeLlama2(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeLlama2(prompt)
	if err != nil {
		panic(err)
	}
	log.Printf("\nLlama 2    : %v\n\n", strings.TrimSpace(completion))
}

func (scenario InvokeModelsScenario) InvokeWithResponseStream(prompt string) {
	log.Println("\nClaude with response stream:")
	_, err := scenario.responseStreamWrapper.InvokeModelWithResponseStream(prompt)
	if err != nil {
		panic(err)
	}
	log.Println()
}

func (scenario InvokeModelsScenario) InvokeTitanImage(prompt string, seed int64) {
	base64ImageData, err := scenario.invokeModelWrapper.InvokeTitanImage(prompt, seed)
	if err != nil {
		panic(err)
	}
	imagePath := saveImage(base64ImageData, "amazon.titan-image-generator-v1")
	fmt.Printf("The generated image has been saved to %s\n", imagePath)
}

func (scenario InvokeModelsScenario) InvokeTitanText(prompt string) {
	completion, err := scenario.invokeModelWrapper.InvokeTitanText(prompt)
	if err != nil {
		panic(err)
	}
	log.Printf("\nTitan Text Express    : %v\n\n", strings.TrimSpace(completion))
}

// snippet-end:[gov2.bedrock-runtime.Scenario_InvokeModels]

func saveImage(base64ImageData string, modelId string) string {
	outputDir := "output"

	if _, err := os.Stat(outputDir); os.IsNotExist(err) {
		err = os.MkdirAll(outputDir, 0755)
		if err != nil {
			log.Panicln("Couldn't create output folder: ", err)
		}
	}

	i := 1
	for {
		if _, err := os.Stat(filepath.Join(outputDir, fmt.Sprintf("%s_%d.png", modelId, i))); os.IsNotExist(err) {
			break
		}
		i++
	}

	imageData, _ := base64.StdEncoding.DecodeString(base64ImageData)

	filePath := filepath.Join(outputDir, fmt.Sprintf("%s_%d.png", modelId, i))
	f, _ := os.Create(filePath)
	_, err := f.Write(imageData)
	if err != nil {
		log.Printf("Couldn't write image to file %v: %v\n", filePath, err)
	}
	_ = f.Close()

	return filePath
}
