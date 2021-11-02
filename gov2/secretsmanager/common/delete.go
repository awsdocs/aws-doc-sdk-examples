// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//snippet-sourcedescription:[Delete a secret in the AWS Secrets Manager ]
//snippet-keyword:[secretsmanager]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/27/2021]
//snippet-sourceauthor:[gangwere]
package common

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
)

//snippet-start:[secretsmanager.go-v2.DeleteSecret]

func DeleteSecret(config aws.Config, arn string) error {
	conn := secretsmanager.NewFromConfig(config)

	_, err := conn.DeleteSecret(context.TODO(), &secretsmanager.DeleteSecretInput{
		SecretId: aws.String(arn),
	})
	return err
}

//snippet-end:[secretsmanager.go-v2.DeleteSecret]
