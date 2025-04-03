// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.bedrock-runtime.Converse.complete]
// snippet-start:[gov2.bedrock-runtime.Converse.struct]

import (
	"context"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime/types"
)

// ConverseWrapper encapsulates Amazon Bedrock actions used in the examples.
// It contains a Bedrock Runtime client that is used to invoke Bedrock.
type ConverseWrapper struct {
	BedrockRuntimeClient *bedrockruntime.Client
}

// snippet-end:[gov2.bedrock-runtime.Converse.struct]

// snippet-start:[gov2.bedrock-runtime.ConverseClaude]

func (wrapper ConverseWrapper) ConverseClaude(ctx context.Context, prompt string) (string, error) {
	var content = types.ContentBlockMemberText{
		Value: prompt,
	}
	var message = types.Message{
		Content: []types.ContentBlock{&content},
		Role:    "user",
	}
	modelId := "anthropic.claude-3-haiku-20240307-v1:0"
	var converseInput = bedrockruntime.ConverseInput{
		ModelId:  aws.String(modelId),
		Messages: []types.Message{message},
	}
	response, err := wrapper.BedrockRuntimeClient.Converse(ctx, &converseInput)
	if err != nil {
		ProcessError(err, modelId)
	}

	responseText, _ := response.Output.(*types.ConverseOutputMemberMessage)
	responseContentBlock := responseText.Value.Content[0]
	text, _ := responseContentBlock.(*types.ContentBlockMemberText)
	return text.Value, nil

}

// snippet-end:[gov2.bedrock-runtime.ConverseClaude]
// snippet-end:[gov2.bedrock-runtime.Converse.complete]
