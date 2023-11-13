// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the foundation model actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/service/bedrock"
	"github.com/aws/aws-sdk-go-v2/service/bedrock/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListFoundationModels(raiseErr *testtools.StubError) testtools.Stub {
	var modelSummaries []types.FoundationModelSummary
	return testtools.Stub{
		OperationName: "ListFoundationModels",
		Input:         &bedrock.ListFoundationModelsInput{},
		Output:        &bedrock.ListFoundationModelsOutput{ModelSummaries: modelSummaries},
		Error:         raiseErr,
	}
}