//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
//SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"fmt"

	"example.aws/go-v2/examples/secretsmanager/common"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/google/uuid"
)

func main() {

	var secretArn string
	var secretName string
	var value string

	cfg, err := config.LoadDefaultConfig(context.TODO())

	if err != nil {
		panic("Couldn't load config!")
	}

	secretName = uuid.NewString()
	value = "s00pers33kr1t"

	if secretArn, err = common.CreateSecret(cfg, secretName, value); err != nil {
		panic("Couldn't create secret!: " + err.Error())
	}
	fmt.Printf("Created the arn %v\n", secretArn)

	if value, err = common.GetSecret(cfg, secretArn); err != nil {
		panic("Couldn't get secret value!")
	}
	fmt.Printf("it has the value \"%v\"\n", value)

	if err = common.UpdateSecret(cfg, secretArn, "correct horse battery staple"); err != nil {
		panic("Couldn't update secret!")
	}
	fmt.Println("The secret has been updated.")

	if value, err = common.GetSecret(cfg, secretArn); err != nil {
		panic("Couldn't get secret value!")
	}
	fmt.Printf("it has the value \"%v\"\n", value)

	var secretIds []string

	if secretIds, err = common.ListSecrets(cfg); err != nil {
		panic("Couldn't list secrets!")
	}

	fmt.Printf("There are %v secrets -- here's their IDs: \n", len(secretIds))
	for _, id := range secretIds {
		fmt.Println(id)
	}

	if err = common.DeleteSecret(cfg, secretArn); err != nil {
		panic("Couldn't delete secret!")
	}
	fmt.Printf("Deleted the secret with arn %v\n", secretArn)
}
