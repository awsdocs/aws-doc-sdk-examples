// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the Bedrock Runtime actions.

package stubs

import (
	"encoding/json"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

type ClaudeResponse struct {
	Completion string `json:"completion"`
}

func StubInvokeClaude(requestBytes []byte, raiseErr *testtools.StubError) testtools.Stub {
	fakeClaudeResponse := ClaudeResponse{
		Completion: "A fake response",
	}

	responseBytes, err := json.Marshal(fakeClaudeResponse)
	if err != nil {
		panic(err)
	}

	return testtools.Stub{
		OperationName: "InvokeModel",
		Input:	&bedrockruntime.InvokeModelInput{
			Body:        requestBytes,
			ModelId:     aws.String("anthropic.claude-v2"),
			ContentType: aws.String("application/json"),
		},
		Output:	&bedrockruntime.InvokeModelOutput{
			Body:        responseBytes,
		},
		Error:	raiseErr,
	}
}

type Jurassic2Response struct { Completions []Completion `json:"completions"` }
type Completion struct { Data Data `json:"data"` }
type Data struct { Text string `json:"text"` }

func StubInvokeJurassic2(requestBytes []byte, raiseErr *testtools.StubError) testtools.Stub {
	fakeJurassicResponse := Jurassic2Response{
		Completions: []Completion{
			{
				Data: Data{
					Text: "A fake response",
				},
			},
		},
	}

	responseBytes, err := json.Marshal(fakeJurassicResponse)
	if err != nil {
		panic(err)
	}

	return testtools.Stub{
		OperationName: "InvokeModel",
		Input:	&bedrockruntime.InvokeModelInput{
			Body:        requestBytes,
			ModelId:     aws.String("ai21.j2-mid-v1"),
			ContentType: aws.String("application/json"),
		},
		Output:	&bedrockruntime.InvokeModelOutput{
			Body:        responseBytes,
		},
		Error:	raiseErr,
	}
}

type Llama2Response struct {
	Generation string `json:"generation"`
}

func StubInvokeLlama2(requestBytes []byte, raiseErr *testtools.StubError) testtools.Stub {
	fakeLlamaResponse := Llama2Response{
		Generation: "A fake response",
	}

	responseBytes, err := json.Marshal(fakeLlamaResponse)
	if err != nil {
		panic(err)
	}

	return testtools.Stub{
		OperationName: "InvokeModel",
		Input:	&bedrockruntime.InvokeModelInput{
			Body:        requestBytes,
			ModelId:     aws.String("meta.llama2-13b-chat-v1"),
			ContentType: aws.String("application/json"),
		},
		Output:	&bedrockruntime.InvokeModelOutput{
			Body:        responseBytes,
		},
		Error:	raiseErr,
	}
}