// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the Bedrock Runtime actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

type StubConverseParams struct {
	ModelId  string
	Messages []types.Message
	Result   types.Message
	RaiseErr *testtools.StubError
}

func StubConverse(params StubConverseParams) testtools.Stub {
	return testtools.Stub{
		OperationName: "Converse",
		Input: &bedrockruntime.ConverseInput{
			ModelId:  aws.String(params.ModelId),
			Messages: params.Messages,
		},
		Output: &bedrockruntime.ConverseOutput{
			Output: &types.ConverseOutputMemberMessage{Value: params.Result},
		},
		Error: params.RaiseErr,
	}
}
