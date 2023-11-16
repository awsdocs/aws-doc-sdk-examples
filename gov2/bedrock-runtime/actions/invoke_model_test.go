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
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrockruntime/stubs"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

const region ="us-east-1"
const text_prompt = "A test prompt"

func CallInvokeModelActions(sdkConfig aws.Config, ) {
    defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	client := bedrockruntime.NewFromConfig(sdkConfig)
	wrapper := InvokeModelWrapper{client}

	completion, err := wrapper.InvokeClaude(text_prompt)
	if err != nil {panic(err)}
	log.Println(completion)

	
    log.Printf("Thanks for watching!")
}

func TestInvokeClaude(t *testing.T) {
    scenTest := InvokeModelActionsTest{}
    testtools.RunScenarioTests(&scenTest, t)
}

type InvokeModelActionsTest struct {}

func fakeClaudeRequest() ([]byte) {
	claudeRequest := ClaudeRequest{
		Prompt:            "Human: " + text_prompt + "\n\nAssistant:",
		MaxTokensToSample: 200,
		Temperature:       0.5,
		StopSequences:     []string{"\n\nHuman:"},
	}
	requestBytes, _ := json.Marshal(claudeRequest)
	return requestBytes
} 

func (scenTest *InvokeModelActionsTest) SetupDataAndStubs() []testtools.Stub {
    var stubList []testtools.Stub
    stubList = append(stubList, stubs.StubInvokeClaude(fakeClaudeRequest(), nil))
    return stubList
}

func (scenTest *InvokeModelActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	  CallInvokeModelActions(*stubber.SdkConfig)
}

func (scenTest *InvokeModelActionsTest) Cleanup() {}