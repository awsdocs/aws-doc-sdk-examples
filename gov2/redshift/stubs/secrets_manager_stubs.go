// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"fmt"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubGetSecretValue(secretId string, userName string, userPassword string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "GetSecretValue",
		Input: &secretsmanager.GetSecretValueInput{
			SecretId: aws.String(secretId),
		},
		Output: &secretsmanager.GetSecretValueOutput{
			SecretString: aws.String(fmt.Sprintf(`{"userName": "%s", "userPassword": "%s"}`, userName, userPassword)),
		},
		Error: raiseErr,
	}
}
