// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the bedrock runtime actions.

package actions

import (
    "testing"
	"encoding/json"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

const prompt = "A test prompt"

func CallInvokeModelActions(sdkConfig aws.Config, ) {
    defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	client := bedrockruntime.NewFromConfig(sdkConfig)
	wrapper := InvokeModelWrapper{client}

	claudeCompletion, err := wrapper.InvokeClaude(prompt)
	if err != nil {panic(err)}
	log.Println(claudeCompletion)
	
	jurassic2Completion, err := wrapper.InvokeJurassic2(prompt)
	if err != nil {panic(err)}
	log.Println(jurassic2Completion)
	
    log.Printf("Thanks for watching!")
}

func TestInvokeModels(t *testing.T) {
    scenTest := InvokeModelActionsTest{}
    testtools.RunScenarioTests(&scenTest, t)
}

type InvokeModelActionsTest struct {}

func (scenTest *InvokeModelActionsTest) SetupDataAndStubs() []testtools.Stub {
    var stubList []testtools.Stub
    stubList = append(stubList, stubs.StubInvokeClaude(fakeClaudeRequest(), nil))
	stubList = append(stubList, stubs.StubInvokeJurassic2(fakeJurassic2Request(), nil))
    return stubList
}

func (scenTest *InvokeModelActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	  CallInvokeModelActions(*stubber.SdkConfig)
}

func (scenTest *InvokeModelActionsTest) Cleanup() {}

func fakeClaudeRequest() ([]byte) {
	requestBytes, _ := json.Marshal(ClaudeRequest{
		Prompt:            "Human: " + prompt + "\n\nAssistant:",
		MaxTokensToSample: 200,
		Temperature:       0.5,
		StopSequences:     []string{"\n\nHuman:"},
	})
	return requestBytes
} 

func fakeJurassic2Request() ([]byte) {
	requestBytes, _ := json.Marshal(Jurassic2Request{ 
		Prompt:      prompt,
		MaxTokens:   200,
		Temperature: 0.5,
	})
	return requestBytes
} 