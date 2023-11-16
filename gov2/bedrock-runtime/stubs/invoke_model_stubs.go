// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the Bedrock Runtime actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubInvokeClaude(requestBytes []byte, raiseErr *testtools.StubError) testtools.Stub {
	var responseBytes []byte

	return testtools.Stub{
		OperationName: "InvokeModel",
		Input:		   &bedrockruntime.InvokeModelInput{
			Body: requestBytes,
			ModelId: aws.String("anthropic.claude-v2"),
			ContentType: aws.String("application/json"),
		},
		Output:		   &bedrockruntime.InvokeModelOutput{Body: responseBytes},
		Error:		   raiseErr,
	}
}