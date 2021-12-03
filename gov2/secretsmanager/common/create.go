// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package common

//snippet-keyword:[secretsmanager]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[10/27/2021]
//snippet-sourceauthor:[gangwere]

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
)

// This creates a secret and returns out its ARN
//snippet-start:[secretsmanager.go-v2.CreateSecret]
func CreateSecret(cfg aws.Config, name string, value string) (string, error) {

	conn := secretsmanager.NewFromConfig(cfg)

	result, err := conn.CreateSecret(context.TODO(), &secretsmanager.CreateSecretInput{
		Name: aws.String(name),
		// descriptions are optional
		Description: aws.String("Example Secret for docs"),
		// You must provide either SecretString or SecretBytes.
		// Both is considered invalid.
		SecretString: aws.String(value),
	})

	if err != nil {
		return "", err
	}

	return *result.ARN, err

}

//snippet-end:[secretsmanager.go-v2.CreateSecret]
