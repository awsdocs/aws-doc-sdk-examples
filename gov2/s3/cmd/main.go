// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"flag"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/scenarios"
)

// main loads default AWS credentials and configuration from the ~/.aws folder and runs
// a scenario specified by the `-scenario` flag.
//
// `-scenario` can be one of the following:
//
// * `getstarted`    -  Runs the interactive get started scenario that shows you how to use
//						Amazon Simple Storage Service (Amazon S3) actions to work with
//						S3 buckets and objects.
// * `presigning` - 	Runs the interactive presigning scenario that shows you how to
//						get presigned requests that contain temporary credentials
//						and can be used to make requests from any HTTP client.
func main() {
	scenarioMap := map[string]func(sdkConfig aws.Config){
		"getstarted": runGetStartedScenario,
		"presigning": runPresigningScenario,
	}
	choices := make([]string, len(scenarioMap))
	choiceIndex := 0
	for choice := range scenarioMap {
		choices[choiceIndex] = choice
		choiceIndex++
	}
	scenario := flag.String(
		"scenario", "",
		fmt.Sprintf("The scenario to run. Must be one of %v.", choices))
	flag.Parse()

	if runScenario, ok := scenarioMap[*scenario]; !ok {
		fmt.Printf("'%v' is not a valid scenario.\n", *scenario)
		flag.Usage()
	} else {
		sdkConfig, err := config.LoadDefaultConfig(context.TODO())
		if err != nil {
			log.Fatalf("unable to load SDK config, %v", err)
		}

		log.SetFlags(0)
		runScenario(sdkConfig)
	}
}

func runGetStartedScenario(sdkConfig aws.Config) {
	scenarios.RunGetStartedScenario(sdkConfig, demotools.NewQuestioner())
}

func runPresigningScenario(sdkConfig aws.Config) {
	scenarios.RunPresigningScenario(sdkConfig, demotools.NewQuestioner(), scenarios.HttpRequester{})
}
