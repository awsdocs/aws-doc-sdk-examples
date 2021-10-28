// SPDX-Identifier: Apache 2.0
//snippet-sourcedescription:[Update a secret in the AWS Secrets Manager ]
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

//snippet-start:[gov2.secretsmanager.UpdateSecret]

func UpdateSecret(config aws.Config, arn string, newValue string) error {
	conn := secretsmanager.NewFromConfig(config)

	_, err := conn.UpdateSecret(context.TODO(), &secretsmanager.UpdateSecretInput{
		SecretId:     aws.String(arn),
		SecretString: aws.String(newValue),
	})
	return err
}

//snippet-end:[gov2.secretsmanager.UpdateSecret]
