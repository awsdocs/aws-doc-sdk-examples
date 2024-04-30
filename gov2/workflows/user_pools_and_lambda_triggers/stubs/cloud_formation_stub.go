// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudformation"
	"github.com/aws/aws-sdk-go-v2/service/cloudformation/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubDescribeStacks(stackName string, outputMap map[string]string, raiseErr *testtools.StubError) testtools.Stub {
	stack := types.Stack{
		StackName: aws.String(stackName),
	}
	for key, value := range outputMap {
		stack.Outputs = append(stack.Outputs, types.Output{OutputKey: aws.String(key), OutputValue: aws.String(value)})
	}
	return testtools.Stub{
		OperationName: "DescribeStacks",
		Input: &cloudformation.DescribeStacksInput{
			StackName: aws.String(stackName),
		},
		Output: &cloudformation.DescribeStacksOutput{
			Stacks: []types.Stack{stack},
		},
		Error: raiseErr,
	}
}
