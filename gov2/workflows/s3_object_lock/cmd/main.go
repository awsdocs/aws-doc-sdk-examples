// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"s3_object_lock/workflows"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
//   - 'object_lock'
//     This scenario demonstrates how to use the AWS SDK for Go V2 to work with Amazon S3 object locking features.
//     It shows how to create S3 buckets with and without object locking enabled, set object lock configurations
//     for individual objects, and interact with locked objects by attempting to delete or overwrite them.
//     The scenario also demonstrates how to set a default retention period for a bucket and view the object
//     lock configurations for individual objects.
func main() {
	scenarioMap := map[string]func(ctx context.Context, sdkConfig aws.Config){
		"object_lock": runObjectLockScenario,
	}
	choices := make([]string, len(scenarioMap))
	choiceIndex := 0
	for choice := range scenarioMap {
		choices[choiceIndex] = choice
		choiceIndex++
	}
	scenario := flag.String(
		"scenario", choices[0],
		fmt.Sprintf("The scenario to run. Must be one of %v.", choices))
	flag.Parse()

	if runScenario, ok := scenarioMap[*scenario]; !ok {
		fmt.Printf("'%v' is not a valid scenario.\n", *scenario)
		flag.Usage()
	} else {
		ctx := context.Background()
		sdkConfig, err := config.LoadDefaultConfig(ctx)
		if err != nil {
			log.Fatalf("unable to load SDK config, %v", err)
		}

		log.SetFlags(0)
		runScenario(ctx, sdkConfig)
	}
}

func runObjectLockScenario(ctx context.Context, sdkConfig aws.Config) {
	questioner := demotools.NewQuestioner()
	scenario := workflows.NewObjectLockScenario(sdkConfig, questioner)
	scenario.Run(ctx)
}
