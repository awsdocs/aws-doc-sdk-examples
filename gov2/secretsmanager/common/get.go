// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
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

//snippet-start:[secretsmanager.go-v2.GetSecret]
// Retrieve the plaintext of a secret given its identifier (ARN or name)
func GetSecret(config aws.Config, secretId string) (string, error) {
	conn := secretsmanager.NewFromConfig(config)

	result, err := conn.GetSecretValue(context.TODO(), &secretsmanager.GetSecretValueInput{
		SecretId: aws.String(secretId),
	})

	if err != nil {
		return "", err
	}

	return *result.SecretString, err
}

//snippet-end:[secretsmanager.go-v2.GetSecret]
