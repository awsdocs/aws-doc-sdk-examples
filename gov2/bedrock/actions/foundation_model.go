// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.bedrock.FoundationModelWrapper.complete]
// snippet-start:[gov2.bedrock.FoundationModelWrapper.struct]

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/service/bedrock"
	"github.com/aws/aws-sdk-go-v2/service/bedrock/types"
)

// FoundationModelWrapper encapsulates Amazon Bedrock actions used in the examples.
// It contains a Bedrock service client that is used to perform foundation model actions.
type FoundationModelWrapper struct {
	BedrockClient *bedrock.Client
}

// snippet-end:[gov2.bedrock.FoundationModelWrapper.struct]

// snippet-start:[gov2.bedrock.ListFoundationModels]

// ListPolicies lists Bedrock foundation models that you can use.
func (wrapper FoundationModelWrapper) ListFoundationModels(ctx context.Context) ([]types.FoundationModelSummary, error) {

	var models []types.FoundationModelSummary

	result, err := wrapper.BedrockClient.ListFoundationModels(ctx, &bedrock.ListFoundationModelsInput{})

	if err != nil {
		log.Printf("Couldn't list foundation models. Here's why: %v\n", err)
	} else {
		models = result.ModelSummaries
	}
	return models, err
}

// snippet-end:[gov2.bedrock.ListFoundationModels]

// snippet-end:[gov2.bedrock.FoundationModelWrapper.complete]
