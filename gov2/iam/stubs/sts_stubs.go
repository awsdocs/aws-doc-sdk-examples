// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Defines stubs used for unit testing the AWS Security Token Service (AWS STS) actions.

package stubs

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/aws/aws-sdk-go-v2/service/sts/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubAssumeRole(roleArn string, sessionName string, lifetime int32,
		keyId string, keySecret string, token string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "AssumeRole",
		Input:         &sts.AssumeRoleInput{
			RoleArn:           aws.String(roleArn),
			RoleSessionName:   aws.String(sessionName),
			DurationSeconds:   aws.Int32(lifetime),
		},
		Output:        &sts.AssumeRoleOutput{Credentials: &types.Credentials{
			AccessKeyId:     aws.String(keyId),
			SecretAccessKey: aws.String(keySecret),
			SessionToken:    aws.String(token),
		}},
		Error:         raiseErr,
	}
}