// SPDX-Identifier: Apache 2.0
package common

//snippet-sourcedescription:[Create a secret in the AWS Secrets Manager]
//snippet-keyword:[secretsmanager]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[10/27/2021]
//snippet-sourceauthor:[gangwere]

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
)

// This creates a secret and prints out its ARN
//snippet-start:[secretsmanager.go-v2.CreateSecret]
func CreateSecret(cfg aws.Config) (string, error) {

	conn := secretsmanager.NewFromConfig(cfg)

	result, err := conn.CreateSecret(context.TODO(), &secretsmanager.CreateSecretInput{
		//You can assign a friendly name to a secret if you wish
		//Name:         aws.String("MySecret"),
		// descriptions are optional
		Description: aws.String("Example Secret for docs"),
		// You must provide either SecretString or SecretBytes.
		// Both is considered invalid.
		SecretString: aws.String("S00p3rs33kr1t123"),
	})

	return *result.ARN, err

}

//snippet-end:[secretsmanager.go-v2.CreateSecret]
