// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.iam.GroupWrapper.complete]
// snippet-start:[gov2.iam.GroupWrapper.struct]

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

// GroupWrapper encapsulates AWS Identity and Access Management (IAM) group actions
// used in the examples.
// It contains an IAM service client that is used to perform group actions.
type GroupWrapper struct {
	IamClient *iam.Client
}

// snippet-end:[gov2.iam.GroupWrapper.struct]

// snippet-start:[gov2.iam.ListGroups]

// ListGroups lists up to maxGroups number of groups.
func (wrapper GroupWrapper) ListGroups(ctx context.Context, maxGroups int32) ([]types.Group, error) {
	var groups []types.Group
	result, err := wrapper.IamClient.ListGroups(ctx, &iam.ListGroupsInput{
		MaxItems: aws.Int32(maxGroups),
	})
	if err != nil {
		log.Printf("Couldn't list groups. Here's why: %v\n", err)
	} else {
		groups = result.Groups
	}
	return groups, err
}

// snippet-end:[gov2.iam.ListGroups]
// snippet-end:[gov2.iam.GroupWrapper.complete]
