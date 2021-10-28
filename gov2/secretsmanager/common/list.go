// SPDX-Identifier: Apache 2.0
//snippet-sourcedescription:[List secrets kept in the AWS Secrets Manager service]
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

//snippet-start:[gov2.secretsmanager.ListSecrets]

func ListSecrets(config aws.Config) ([]string, error) {
	conn := secretsmanager.NewFromConfig(config)
	secrets := make([]string, 0)
	result, err := conn.ListSecrets(context.Background(), &secretsmanager.ListSecretsInput{})
	if err == nil {
		for _, secret := range result.SecretList {
			secrets = append(secrets, *secret.ARN)
		}
	}
	return secrets, err
}

//snippet-end:[gov2.secretsmanager.ListSecrets]
