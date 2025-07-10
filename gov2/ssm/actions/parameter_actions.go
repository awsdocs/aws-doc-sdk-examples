// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
)

// ParameterWrapper encapsulates AWS Systems Manager parameter operations.
// This wrapper simplifies calling the SSM service from your applications.
type ParameterWrapper struct {
	SSMClient *ssm.Client
}

// snippet-start:[ssm.go.PutParameter]
// PutParameter creates or updates a parameter in AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) PutParameter(ctx context.Context, name, value string, paramType types.ParameterType) error {
	_, err := wrapper.SSMClient.PutParameter(ctx, &ssm.PutParameterInput{
		Name:      aws.String(name),
		Value:     aws.String(value),
		Type:      paramType,
		Overwrite: aws.Bool(true),
	})
	if err != nil {
		return fmt.Errorf("couldn't put parameter %s: %w", name, err)
	}

	log.Printf("Successfully put parameter: %s", name)
	return nil
}

// snippet-end:[ssm.go.PutParameter]

// snippet-start:[ssm.go.GetParameter]
// GetParameter retrieves a parameter from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) GetParameter(ctx context.Context, name string, withDecryption bool) (*types.Parameter, error) {
	result, err := wrapper.SSMClient.GetParameter(ctx, &ssm.GetParameterInput{
		Name:           aws.String(name),
		WithDecryption: aws.Bool(withDecryption),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't get parameter %s: %w", name, err)
	}

	return result.Parameter, nil
}

// snippet-end:[ssm.go.GetParameter]

// snippet-start:[ssm.go.GetParameters]
// GetParameters retrieves multiple parameters from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) GetParameters(ctx context.Context, names []string, withDecryption bool) ([]types.Parameter, error) {
	result, err := wrapper.SSMClient.GetParameters(ctx, &ssm.GetParametersInput{
		Names:          names,
		WithDecryption: aws.Bool(withDecryption),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't get parameters: %w", err)
	}

	if len(result.InvalidParameters) > 0 {
		log.Printf("Warning: Invalid parameters found: %v", result.InvalidParameters)
	}

	return result.Parameters, nil
}

// snippet-end:[ssm.go.GetParameters]

// snippet-start:[ssm.go.GetParametersByPath]
// GetParametersByPath retrieves parameters by path from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) GetParametersByPath(ctx context.Context, path string, recursive, withDecryption bool) ([]types.Parameter, error) {
	var allParameters []types.Parameter
	input := &ssm.GetParametersByPathInput{
		Path:           aws.String(path),
		Recursive:      aws.Bool(recursive),
		WithDecryption: aws.Bool(withDecryption),
		MaxResults:     aws.Int32(10),
	}

	paginator := ssm.NewGetParametersByPathPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't get parameters by path %s: %w", path, err)
		}
		allParameters = append(allParameters, result.Parameters...)
	}

	return allParameters, nil
}

// snippet-end:[ssm.go.GetParametersByPath]

// snippet-start:[ssm.go.DeleteParameter]
// DeleteParameter deletes a parameter from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) DeleteParameter(ctx context.Context, name string) error {
	_, err := wrapper.SSMClient.DeleteParameter(ctx, &ssm.DeleteParameterInput{
		Name: aws.String(name),
	})
	if err != nil {
		return fmt.Errorf("couldn't delete parameter %s: %w", name, err)
	}

	log.Printf("Successfully deleted parameter: %s", name)
	return nil
}

// snippet-end:[ssm.go.DeleteParameter]

// snippet-start:[ssm.go.DeleteParameters]
// DeleteParameters deletes multiple parameters from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) DeleteParameters(ctx context.Context, names []string) ([]string, []string, error) {
	result, err := wrapper.SSMClient.DeleteParameters(ctx, &ssm.DeleteParametersInput{
		Names: names,
	})
	if err != nil {
		return nil, nil, fmt.Errorf("couldn't delete parameters: %w", err)
	}

	log.Printf("Successfully deleted %d parameters", len(result.DeletedParameters))
	if len(result.InvalidParameters) > 0 {
		log.Printf("Warning: %d parameters could not be deleted", len(result.InvalidParameters))
	}

	return result.DeletedParameters, result.InvalidParameters, nil
}

// snippet-end:[ssm.go.DeleteParameters]

// snippet-start:[ssm.go.GetParameterHistory]
// GetParameterHistory retrieves the history of a parameter from AWS Systems Manager Parameter Store.
func (wrapper ParameterWrapper) GetParameterHistory(ctx context.Context, name string, withDecryption bool) ([]types.ParameterHistory, error) {
	var allHistory []types.ParameterHistory
	input := &ssm.GetParameterHistoryInput{
		Name:           aws.String(name),
		WithDecryption: aws.Bool(withDecryption),
		MaxResults:     aws.Int32(10),
	}

	paginator := ssm.NewGetParameterHistoryPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't get parameter history for %s: %w", name, err)
		}
		allHistory = append(allHistory, result.Parameters...)
	}

	return allHistory, nil
}

// snippet-end:[ssm.go.GetParameterHistory]
