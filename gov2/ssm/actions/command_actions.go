// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/ssm"
	"github.com/aws/aws-sdk-go-v2/service/ssm/types"
)

// CommandWrapper encapsulates AWS Systems Manager Run Command operations.
// This wrapper simplifies calling the SSM service from your applications.
type CommandWrapper struct {
	SSMClient *ssm.Client
}

// snippet-start:[ssm.go.SendCommand]
// SendCommand sends a command to one or more EC2 instances using SSM Run Command.
func (wrapper CommandWrapper) SendCommand(ctx context.Context, documentName string, instanceIds []string, parameters map[string][]string) (*types.Command, error) {
	input := &ssm.SendCommandInput{
		DocumentName: aws.String(documentName),
		InstanceIds:  instanceIds,
	}

	if parameters != nil {
		input.Parameters = parameters
	}

	result, err := wrapper.SSMClient.SendCommand(ctx, input)
	if err != nil {
		return nil, fmt.Errorf("couldn't send command %s: %w", documentName, err)
	}

	log.Printf("Successfully sent command %s to %d instances", documentName, len(instanceIds))
	return result.Command, nil
}

// snippet-end:[ssm.go.SendCommand]

// snippet-start:[ssm.go.ListCommands]
// ListCommands lists commands that have been executed.
func (wrapper CommandWrapper) ListCommands(ctx context.Context, instanceId string, maxResults int32) ([]types.Command, error) {
	input := &ssm.ListCommandsInput{
		MaxResults: aws.Int32(maxResults),
	}

	if instanceId != "" {
		input.InstanceId = aws.String(instanceId)
	}

	var allCommands []types.Command
	paginator := ssm.NewListCommandsPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't list commands: %w", err)
		}
		allCommands = append(allCommands, result.Commands...)
	}

	return allCommands, nil
}

// snippet-end:[ssm.go.ListCommands]

// snippet-start:[ssm.go.GetCommandInvocation]
// GetCommandInvocation gets the details of a command invocation for a specific instance.
func (wrapper CommandWrapper) GetCommandInvocation(ctx context.Context, commandId, instanceId string) (*ssm.GetCommandInvocationOutput, error) {
	result, err := wrapper.SSMClient.GetCommandInvocation(ctx, &ssm.GetCommandInvocationInput{
		CommandId:  aws.String(commandId),
		InstanceId: aws.String(instanceId),
	})
	if err != nil {
		return nil, fmt.Errorf("couldn't get command invocation for command %s on instance %s: %w", commandId, instanceId, err)
	}

	return result, nil
}

// snippet-end:[ssm.go.GetCommandInvocation]

// snippet-start:[ssm.go.ListCommandInvocations]
// ListCommandInvocations lists the invocations of a command.
func (wrapper CommandWrapper) ListCommandInvocations(ctx context.Context, commandId string, maxResults int32) ([]types.CommandInvocation, error) {
	input := &ssm.ListCommandInvocationsInput{
		MaxResults: aws.Int32(maxResults),
	}

	if commandId != "" {
		input.CommandId = aws.String(commandId)
	}

	var allInvocations []types.CommandInvocation
	paginator := ssm.NewListCommandInvocationsPaginator(wrapper.SSMClient, input)
	for paginator.HasMorePages() {
		result, err := paginator.NextPage(ctx)
		if err != nil {
			return nil, fmt.Errorf("couldn't list command invocations: %w", err)
		}
		allInvocations = append(allInvocations, result.CommandInvocations...)
	}

	return allInvocations, nil
}

// snippet-end:[ssm.go.ListCommandInvocations]

// snippet-start:[ssm.go.CancelCommand]
// CancelCommand cancels a command that is currently running.
func (wrapper CommandWrapper) CancelCommand(ctx context.Context, commandId string, instanceIds []string) error {
	input := &ssm.CancelCommandInput{
		CommandId: aws.String(commandId),
	}

	if len(instanceIds) > 0 {
		input.InstanceIds = instanceIds
	}

	_, err := wrapper.SSMClient.CancelCommand(ctx, input)
	if err != nil {
		return fmt.Errorf("couldn't cancel command %s: %w", commandId, err)
	}

	log.Printf("Successfully cancelled command: %s", commandId)
	return nil
}

// snippet-end:[ssm.go.CancelCommand]

// snippet-start:[ssm.go.WaitForCommandCompletion]
// WaitForCommandCompletion waits for a command to complete on all target instances.
func (wrapper CommandWrapper) WaitForCommandCompletion(ctx context.Context, commandId string, instanceIds []string, maxWaitTime time.Duration) error {
	timeout := time.After(maxWaitTime)
	ticker := time.NewTicker(5 * time.Second)
	defer ticker.Stop()

	for {
		select {
		case <-timeout:
			return fmt.Errorf("command %s did not complete within %v", commandId, maxWaitTime)
		case <-ticker.C:
			allCompleted := true
			for _, instanceId := range instanceIds {
				invocation, err := wrapper.GetCommandInvocation(ctx, commandId, instanceId)
				if err != nil {
					// If we can't get the invocation, the command might still be running
					allCompleted = false
					break
				}

				status := invocation.Status
				if status == types.CommandInvocationStatusInProgress || 
				   status == types.CommandInvocationStatusPending ||
				   status == types.CommandInvocationStatusDelayed {
					allCompleted = false
					break
				}
			}

			if allCompleted {
				log.Printf("Command %s completed on all instances", commandId)
				return nil
			}
		}
	}
}

// snippet-end:[ssm.go.WaitForCommandCompletion]

// snippet-start:[ssm.go.RunShellCommand]
// RunShellCommand is a convenience method to run a shell command on EC2 instances.
func (wrapper CommandWrapper) RunShellCommand(ctx context.Context, instanceIds []string, commands []string) (*types.Command, error) {
	parameters := map[string][]string{
		"commands": commands,
	}

	return wrapper.SendCommand(ctx, "AWS-RunShellScript", instanceIds, parameters)
}

// snippet-end:[ssm.go.RunShellCommand]

// snippet-start:[ssm.go.RunPowerShellCommand]
// RunPowerShellCommand is a convenience method to run PowerShell commands on Windows EC2 instances.
func (wrapper CommandWrapper) RunPowerShellCommand(ctx context.Context, instanceIds []string, commands []string) (*types.Command, error) {
	parameters := map[string][]string{
		"commands": commands,
	}

	return wrapper.SendCommand(ctx, "AWS-RunPowerShellScript", instanceIds, parameters)
}

// snippet-end:[ssm.go.RunPowerShellCommand]
