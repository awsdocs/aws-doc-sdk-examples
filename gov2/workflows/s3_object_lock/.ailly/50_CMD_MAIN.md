---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  prompt: |
    Write a main function that runs the scenario.
    Include a comment that describes the scenario.
    Here is a main function from a different workflow.
    <example>
      // main loads default AWS credentials and configuration from the ~/.aws folder and runs
      // a scenario specified by the `-scenario` flag.
      //
      // `-scenario` can be one of the following:
      //
      //   - `topics_and_queues` -  Runs the interactive topics and queues scenario that shows
      //     you how to create an Amazon SNS topic and Amazon SQS queues and publish messages
      //     to the topic that are forwarded to the subscribed queues.
      func main() {
      scenarioMap := map[string]func(sdkConfig aws.Config){
      "topics_and_queues": runTopicsAndQueuesScenario,
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
      
      func runTopicsAndQueuesScenario(sdkConfig aws.Config) {
      workflows.RunTopicsAndQueuesScenario(sdkConfig, demotools.NewQuestioner())
      }
    </example>
  ---
---
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// This scenario demonstrates how to use the AWS SDK for Go to work with Amazon S3 object locking features.
// It shows how to create S3 buckets with and without object locking enabled, set object lock configurations
// for individual objects, and interact with locked objects by attempting to delete or overwrite them.
// The scenario also demonstrates how to set a default retention period for a bucket and view the object
// lock configurations for individual objects.

package main

import (
    "context"
    "flag"
    "fmt"
    "log"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/s3"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/workflows"
)

func main() {
    scenario := flag.String("scenario", "", "The scenario to run. Must be 'object_lock'.")
    flag.Parse()

    if *scenario != "object_lock" {
        fmt.Printf("'%v' is not a valid scenario.\n", *scenario)
        flag.Usage()
        return
    }

    sdkConfig, err := config.LoadDefaultConfig(context.TODO())
    if err != nil {
        log.Fatalf("unable to load SDK config, %v", err)
    }

    log.SetFlags(0)
    runObjectLockScenario(sdkConfig)
}

func runObjectLockScenario(sdkConfig config.Config) {
    s3Client := s3.NewFromConfig(sdkConfig)
    helper := workflows.NewScenarioHelper(s3Client)
    questioner := demotools.NewQuestioner()

    scenario := workflows.NewObjectLockScenario(sdkConfig, questioner, helper)
    scenario.Run()
}
```

This `main` function sets up the scenario and runs the `ObjectLockScenario` workflow. Here's a breakdown of what it does:

1. It defines a command-line flag `-scenario` that accepts the value `"object_lock"`.
2. If the provided scenario is not `"object_lock"`, it prints an error message and exits.
3. It loads the default AWS configuration from the `~/.aws` folder using `config.LoadDefaultConfig`.
4. It creates an `s3.Client` instance using the loaded configuration.
5. It creates a `ScenarioHelper` and a `Questioner` instance, which are used by the `ObjectLockScenario` workflow.
6. It creates a new `ObjectLockScenario` instance with the configuration, questioner, and helper.
7. It runs the `ObjectLockScenario` by calling its `Run` method.

The `runObjectLockScenario` function is responsible for setting up the necessary dependencies and running the scenario. It creates an `s3.Client` instance, a `ScenarioHelper`, and a `Questioner`, then passes them to the `NewObjectLockScenario` function to create a new scenario instance. Finally, it calls the `Run` method on the scenario instance to execute the workflow.

Note that this implementation assumes the existence of the `ScenarioHelper` struct and its `NewScenarioHelper` constructor function, which should be defined in the `workflows` package.