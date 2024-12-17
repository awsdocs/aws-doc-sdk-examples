// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.iam.PolicyWrapper.complete]
// snippet-start:[gov2.iam.PolicyWrapper.struct]

import (
	"context"
	"encoding/json"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// PolicyWrapper encapsulates AWS Identity and Access Management (IAM) policy actions
// used in the examples.
// It contains an IAM service client that is used to perform policy actions.
type PolicyWrapper struct {
	IamClient *iam.Client
}

// snippet-end:[gov2.iam.PolicyWrapper.struct]

// snippet-start:[gov2.iam.ListPolicies]

// ListPolicies gets up to maxPolicies policies.
func (wrapper PolicyWrapper) ListPolicies(ctx context.Context, maxPolicies int32) ([]types.Policy, error) {
	var policies []types.Policy
	result, err := wrapper.IamClient.ListPolicies(ctx, &iam.ListPoliciesInput{
		MaxItems: aws.Int32(maxPolicies),
	})
	if err != nil {
		log.Printf("Couldn't list policies. Here's why: %v\n", err)
	} else {
		policies = result.Policies
	}
	return policies, err
}

// snippet-end:[gov2.iam.ListPolicies]

// snippet-start:[gov2.iam.CreatePolicy]

// PolicyDocument defines a policy document as a Go struct that can be serialized
// to JSON.
type PolicyDocument struct {
	Version   string
	Statement []PolicyStatement
}

// PolicyStatement defines a statement in a policy document.
type PolicyStatement struct {
	Effect    string
	Action    []string
	Principal map[string]string `json:",omitempty"`
	Resource  *string           `json:",omitempty"`
}

// CreatePolicy creates a policy that grants a list of actions to the specified resource.
// PolicyDocument shows how to work with a policy document as a data structure and
// serialize it to JSON by using Go's JSON marshaler.
func (wrapper PolicyWrapper) CreatePolicy(ctx context.Context, policyName string, actions []string,
	resourceArn string) (*types.Policy, error) {
	var policy *types.Policy
	policyDoc := PolicyDocument{
		Version: "2012-10-17",
		Statement: []PolicyStatement{{
			Effect:   "Allow",
			Action:   actions,
			Resource: aws.String(resourceArn),
		}},
	}
	policyBytes, err := json.Marshal(policyDoc)
	if err != nil {
		log.Printf("Couldn't create policy document for %v. Here's why: %v\n", resourceArn, err)
		return nil, err
	}
	result, err := wrapper.IamClient.CreatePolicy(ctx, &iam.CreatePolicyInput{
		PolicyDocument: aws.String(string(policyBytes)),
		PolicyName:     aws.String(policyName),
	})
	if err != nil {
		log.Printf("Couldn't create policy %v. Here's why: %v\n", policyName, err)
	} else {
		policy = result.Policy
	}
	return policy, err
}

// snippet-end:[gov2.iam.CreatePolicy]

// snippet-start:[gov2.iam.GetPolicy]

// GetPolicy gets data about a policy.
func (wrapper PolicyWrapper) GetPolicy(ctx context.Context, policyArn string) (*types.Policy, error) {
	var policy *types.Policy
	result, err := wrapper.IamClient.GetPolicy(ctx, &iam.GetPolicyInput{
		PolicyArn: aws.String(policyArn),
	})
	if err != nil {
		log.Printf("Couldn't get policy %v. Here's why: %v\n", policyArn, err)
	} else {
		policy = result.Policy
	}
	return policy, err
}

// snippet-end:[gov2.iam.GetPolicy]

// snippet-start:[gov2.iam.DeletePolicy]

// DeletePolicy deletes a policy.
func (wrapper PolicyWrapper) DeletePolicy(ctx context.Context, policyArn string) error {
	_, err := wrapper.IamClient.DeletePolicy(ctx, &iam.DeletePolicyInput{
		PolicyArn: aws.String(policyArn),
	})
	if err != nil {
		log.Printf("Couldn't delete policy %v. Here's why: %v\n", policyArn, err)
	}
	return err
}

// snippet-end:[gov2.iam.DeletePolicy]
// snippet-end:[gov2.iam.PolicyWrapper.complete]
