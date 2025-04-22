// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the bedrock runtime actions.

package actions

import (
	"context"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/bedrock-runtime/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
	"log"
	"testing"
)

const CONVERSE_CLAUDE_MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0"
const CONVERSE_PROMPT = "Converse test prompt"

func CallConverseActions(sdkConfig aws.Config) {
	defer func() {
		if r := recover(); r != nil {
			log.Println(r)
		}
	}()

	client := bedrockruntime.NewFromConfig(sdkConfig)
	wrapper := ConverseWrapper{client}
	ctx := context.Background()

	claudeCompletion, err := wrapper.ConverseClaude(ctx, CONVERSE_PROMPT)
	if err != nil {
		panic(err)
	}
	log.Println(claudeCompletion)

	log.Printf("Thanks for watching!")
}

func TestConverse(t *testing.T) {
	scenTest := ConverseActionsTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

type ConverseActionsTest struct{}

func stubConverse(modelId string) testtools.Stub {
	var content = types.ContentBlockMemberText{
		Value: CONVERSE_PROMPT,
	}
	var message = types.Message{
		Content: []types.ContentBlock{&content},
		Role:    "user",
	}
	var resultContent = types.ContentBlockMemberText{
		Value: "A proper result",
	}
	var result = types.Message{
		Content: []types.ContentBlock{&resultContent},
		Role:    "user",
	}
	return stubs.StubConverse(stubs.StubConverseParams{
		ModelId:  modelId,
		Messages: []types.Message{message},
		Result:   result,
		RaiseErr: nil,
	})
}
func (scenTest *ConverseActionsTest) SetupDataAndStubs() []testtools.Stub {
	var stubList []testtools.Stub
	stubList = append(stubList, stubConverse(CONVERSE_CLAUDE_MODEL_ID))

	return stubList
}

func (scenTest *ConverseActionsTest) RunSubTest(stubber *testtools.AwsmStubber) {
	CallConverseActions(*stubber.SdkConfig)
}

func (scenTest *ConverseActionsTest) Cleanup() {}
