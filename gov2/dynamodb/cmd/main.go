// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/actions"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/dynamodb/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// the movie table scenario.
func main() {
	sdkConfig, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}

	log.SetFlags(0)
	scenarios.RunScenario(
		sdkConfig,
		demotools.NewQuestioner(),
		"doc-example-movie-table",
		actions.MovieSampler{URL: "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip"},
	)
}
