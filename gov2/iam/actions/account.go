// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.iam.AccountWrapper.complete]
// snippet-start:[gov2.iam.AccountWrapper.struct]

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// AccountWrapper encapsulates AWS Identity and Access Management (IAM) account actions
// used in the examples.
// It contains an IAM service client that is used to perform account actions.
type AccountWrapper struct {
	IamClient *iam.Client
}

// snippet-end:[gov2.iam.AccountWrapper.struct]

// snippet-start:[gov2.iam.GetAccountPasswordPolicy]

// GetAccountPasswordPolicy gets the account password policy for the current account.
// If no policy has been set, a NoSuchEntityException is error is returned.
func (wrapper AccountWrapper) GetAccountPasswordPolicy(ctx context.Context) (*types.PasswordPolicy, error) {
	var pwPolicy *types.PasswordPolicy
	result, err := wrapper.IamClient.GetAccountPasswordPolicy(ctx,
		&iam.GetAccountPasswordPolicyInput{})
	if err != nil {
		log.Printf("Couldn't get account password policy. Here's why: %v\n", err)
	} else {
		pwPolicy = result.PasswordPolicy
	}
	return pwPolicy, err
}

// snippet-end:[gov2.iam.GetAccountPasswordPolicy]

// snippet-start:[gov2.iam.ListSAMLProviders]

// ListSAMLProviders gets the SAML providers for the account.
func (wrapper AccountWrapper) ListSAMLProviders(ctx context.Context) ([]types.SAMLProviderListEntry, error) {
	var providers []types.SAMLProviderListEntry
	result, err := wrapper.IamClient.ListSAMLProviders(ctx, &iam.ListSAMLProvidersInput{})
	if err != nil {
		log.Printf("Couldn't list SAML providers. Here's why: %v\n", err)
	} else {
		providers = result.SAMLProviderList
	}
	return providers, err
}

// snippet-end:[gov2.iam.ListSAMLProviders]
// snippet-end:[gov2.iam.AccountWrapper.complete]
