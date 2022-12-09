// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the group actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubListGroups(maxGroups int32, groupName []string, raiseErr *testtools.StubError) testtools.Stub {
	var groups []types.Group
	for _, name := range groupName {
		groups = append(groups, types.Group{GroupName: aws.String(name)})
	}
	return testtools.Stub{
		OperationName: "ListGroups",
		Input:         &iam.ListGroupsInput{MaxItems: aws.Int32(maxGroups)},
		Output:        &iam.ListGroupsOutput{Groups: groups},
		Error:         raiseErr,
	}
}