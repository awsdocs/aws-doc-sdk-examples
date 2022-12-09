// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the policy actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListPolicies(maxPolicies int32, policyNames []string, raiseErr *testtools.StubError) testtools.Stub {
	var policies []types.Policy
	for _, name := range policyNames {
		policies = append(policies, types.Policy{PolicyName: aws.String(name)})
	}
	return testtools.Stub{
		OperationName: "ListPolicies",
		Input:         &iam.ListPoliciesInput{MaxItems: aws.Int32(maxPolicies)},
		Output:        &iam.ListPoliciesOutput{Policies: policies},
		Error:         raiseErr,
	}
}

func StubCreatePolicy(policyName string, policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "CreatePolicy",
		Input:         &iam.CreatePolicyInput{
			PolicyDocument: nil,
			PolicyName:     aws.String(policyName),
		},
		Output:        &iam.CreatePolicyOutput{
			Policy:         &types.Policy{
				Arn:                           aws.String(policyArn),
				PolicyName:                    aws.String(policyName),
			},
		},
		IgnoreFields:  []string{"PolicyDocument"},
		Error:         raiseErr,
	}
}

func StubGetPolicy(policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetPolicy",
		Input:         &iam.GetPolicyInput{PolicyArn: aws.String(policyArn)},
		Output:        &iam.GetPolicyOutput{Policy: &types.Policy{Arn: aws.String(policyArn)}},
		Error:         raiseErr,
	}
}

func StubDeletePolicy(policyArn string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeletePolicy",
		Input:         &iam.DeletePolicyInput{PolicyArn: aws.String(policyArn)},
		Output:        &iam.DeletePolicyOutput{},
		Error:         raiseErr,
	}
}
