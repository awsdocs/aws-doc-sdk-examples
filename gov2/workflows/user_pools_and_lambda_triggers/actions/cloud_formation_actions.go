// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.cloudformation.CloudFormationActions.complete]

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudformation"
)

// StackOutputs defines a map of outputs from a specific stack.
type StackOutputs map[string]string

type CloudFormationActions struct {
	CfnClient *cloudformation.Client
}

// GetOutputs gets the outputs from a CloudFormation stack and puts them into a structured format.
func (actor CloudFormationActions) GetOutputs(ctx context.Context, stackName string) StackOutputs {
	output, err := actor.CfnClient.DescribeStacks(ctx, &cloudformation.DescribeStacksInput{
		StackName: aws.String(stackName),
	})
	if err != nil || len(output.Stacks) == 0 {
		log.Panicf("Couldn't find a CloudFormation stack named %v. Here's why: %v\n", stackName, err)
	}
	stackOutputs := StackOutputs{}
	for _, out := range output.Stacks[0].Outputs {
		stackOutputs[*out.OutputKey] = *out.OutputValue
	}
	return stackOutputs
}

// snippet-end:[gov2.cloudformation.CloudFormationActions.complete]
