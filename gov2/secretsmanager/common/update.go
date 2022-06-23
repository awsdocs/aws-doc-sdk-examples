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

//snippet-start:[secretsmanager.go-v2.UpdateSecret]
// Change the value of a secret, given the secret identifier and a new value. 
func UpdateSecret(config aws.Config, secretId string, newValue string) error {
	conn := secretsmanager.NewFromConfig(config)

	_, err := conn.UpdateSecret(context.TODO(), &secretsmanager.UpdateSecretInput{
		SecretId:     aws.String(secretId),
		SecretString: aws.String(newValue),
	})
	return err
}

//snippet-end:[secretsmanager.go-v2.UpdateSecret]
