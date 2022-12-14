// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the account actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubGetAccountPasswordPolicy(raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetAccountPasswordPolicy",
		Input:         &iam.GetAccountPasswordPolicyInput{},
		Output:        &iam.GetAccountPasswordPolicyOutput{
			PasswordPolicy: &types.PasswordPolicy{},
		},
		Error:         raiseErr,
	}
}

func StubListSAMLProviders(providerArns []string, raiseErr *testtools.StubError) testtools.Stub {
	var providers []types.SAMLProviderListEntry
	for _, arn := range providerArns {
		providers = append(providers, types.SAMLProviderListEntry{Arn: aws.String(arn)})
	}
	return testtools.Stub{
		OperationName: "ListSAMLProviders",
		Input:         &iam.ListSAMLProvidersInput{},
		Output:        &iam.ListSAMLProvidersOutput{SAMLProviderList: providers},
		Error:         raiseErr,
	}
}
