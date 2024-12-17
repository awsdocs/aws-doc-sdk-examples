// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the bedrock runtime actions.

package actions

import (
	"context"
	"encoding/json"
	"log"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

const CLAUDE_MODEL_ID = "anthropic.claude-v2"
const JURASSIC2_MODEL_ID = "ai21.j2-mid-v1"
const TITAN_IMAGE_MODEL_ID = "amazon.titan-image-generator-v1"
const TITAN_TEXT_EXPRESS_MODEL_ID = "amazon.titan-text-express-v1"

const prompt = "A test prompt"

func CallInvokeModelActions(sdkConfig aws.Config) {
	defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	client := bedrockruntime.NewFromConfig(sdkConfig)
	wrapper := InvokeModelWrapper{client}
	ctx := context.Background()

	claudeCompletion, err := wrapper.InvokeClaude(ctx, prompt)
	if err != nil {
		panic(err)
	}
	log.Println(claudeCompletion)

	jurassic2Completion, err := wrapper.InvokeJurassic2(ctx, prompt)
	if err != nil {
		panic(err)
	}
	log.Println(jurassic2Completion)

	seed := int64(0)
	titanImageCompletion, err := wrapper.InvokeTitanImage(ctx, prompt, seed)
	if err != nil {
		panic(err)
	}
	log.Println(titanImageCompletion)

	titanTextCompletion, err := wrapper.InvokeTitanText(ctx, prompt)
	if err != nil {
		panic(err)
	}
	log.Println(titanTextCompletion)

	log.Printf("Thanks for watching!")
}

func TestInvokeModels(t *testing.T) {
	scenTest := InvokeModelActionsTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

type InvokeModelActionsTest struct{}

func (scenTest *InvokeModelActionsTest) SetupDataAndStubs() []testtools.Stub {
	var stubList []testtools.Stub
	stubList = append(stubList, stubInvokeModel(CLAUDE_MODEL_ID))
	stubList = append(stubList, stubInvokeModel(JURASSIC2_MODEL_ID))
	stubList = append(stubList, stubInvokeModel(TITAN_IMAGE_MODEL_ID))
	stubList = append(stubList, stubInvokeModel(TITAN_TEXT_EXPRESS_MODEL_ID))

	return stubList
}

func (scenTest *InvokeModelActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	CallInvokeModelActions(*stubber.SdkConfig)
}

func (scenTest *InvokeModelActionsTest) Cleanup() {}

func stubInvokeModel(modelId string) testtools.Stub {
	var request []byte
	var response []byte

	switch modelId {
	case CLAUDE_MODEL_ID:
		request, _ = json.Marshal(ClaudeRequest{
			Prompt:            "Human: " + prompt + "\n\nAssistant:",
			MaxTokensToSample: 200,
			Temperature:       0.5,
			StopSequences:     []string{"\n\nHuman:"},
		})
		response, _ = json.Marshal(ClaudeResponse{
			Completion: "A fake response",
		})

	case JURASSIC2_MODEL_ID:
		request, _ = json.Marshal(Jurassic2Request{
			Prompt:      prompt,
			MaxTokens:   200,
			Temperature: 0.5,
		})
		response, _ = json.Marshal(Jurassic2Response{
			Completions: []Completion{
				{Data: Data{Text: "A fake response"}},
			},
		})

	case TITAN_IMAGE_MODEL_ID:
		request, _ = json.Marshal(TitanImageRequest{
			TaskType: "TEXT_IMAGE",
			TextToImageParams: TextToImageParams{
				Text: prompt,
			},
			ImageGenerationConfig: ImageGenerationConfig{
				NumberOfImages: 1,
				Quality:        "standard",
				CfgScale:       8.0,
				Height:         512,
				Width:          512,
				Seed:           0,
			},
		})
		response, _ = json.Marshal(TitanImageResponse{
			Images: []string{"FakeBase64String=="},
		})

	case TITAN_TEXT_EXPRESS_MODEL_ID:
		request, _ = json.Marshal(TitanTextRequest{
			InputText: prompt,
			TextGenerationConfig: TextGenerationConfig{
				Temperature:   0,
				TopP:          1,
				MaxTokenCount: 4096,
			},
		})
		response, _ = json.Marshal(TitanTextResponse{
			Results: []Result{
				{
					OutputText: "A fake response",
				},
			},
		})

	default:
		return testtools.Stub{}
	}

	return stubs.StubInvokeModel(stubs.StubInvokeModelParams{
		Request: request, Response: response, ModelId: modelId, RaiseErr: nil,
	})
}
