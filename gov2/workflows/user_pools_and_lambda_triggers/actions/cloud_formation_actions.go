// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudformation"
)

// snippet-start:[gov2.cloudformation.CloudFormationActions.complete]

// StackOutputs defines structured data for the outputs from a specific stack.
type StackOutputs struct {
	AutoConfirmFunction    string
	AutoConfirmFunctionArn string
	MigrateUserFunction    string
	MigrateUserFunctionArn string
	ActivityLogFunction    string
	ActivityLogFunctionArn string
	UserPoolArn            string
	UserPoolClientId       string
	UserPoolId             string
	TableName              string
}

type CloudFormationActions struct {
	CfnClient *cloudformation.Client
}

// GetOutputs gets the outputs from a CloudFormation stack and puts them into a structured format.
func (actor CloudFormationActions) GetOutputs(stackName string) StackOutputs {
	output, err := actor.CfnClient.DescribeStacks(context.TODO(), &cloudformation.DescribeStacksInput{
		StackName: aws.String(stackName),
	})
	if err != nil || len(output.Stacks) == 0 {
		log.Panicf("Couldn't find a CloudFormation stack named %v. Here's why: %v\n", stackName, err)
	}
	stackOutputs := StackOutputs{}
	for _, out := range output.Stacks[0].Outputs {
		switch *out.OutputKey {
		case "AutoConfirmFunction":
			stackOutputs.AutoConfirmFunction = *out.OutputValue
		case "AutoConfirmFunctionArn":
			stackOutputs.AutoConfirmFunctionArn = *out.OutputValue
		case "MigrateUserFunction":
			stackOutputs.MigrateUserFunction = *out.OutputValue
		case "MigrateUserFunctionArn":
			stackOutputs.MigrateUserFunctionArn = *out.OutputValue
		case "ActivityLogFunction":
			stackOutputs.ActivityLogFunction = *out.OutputValue
		case "ActivityLogFunctionArn":
			stackOutputs.ActivityLogFunctionArn = *out.OutputValue
		case "UserPoolArn":
			stackOutputs.UserPoolArn = *out.OutputValue
		case "UserPoolClientId":
			stackOutputs.UserPoolClientId = *out.OutputValue
		case "UserPoolId":
			stackOutputs.UserPoolId = *out.OutputValue
		case "TableName":
			stackOutputs.TableName = *out.OutputValue
		}
	}
	return stackOutputs
}

// snippet-end:[gov2.cloudformation.CloudFormationActions.complete]
