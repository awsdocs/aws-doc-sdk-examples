// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"fmt"

	"go.uber.org/zap"

	"os"
	"strconv"

	"github.com/aws/aws-sdk-go-v2/aws"
	awsConfig "github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/ec2/imds"
)

const defaultMessageVisibilityTimeout = 60

// A Config provides a collection of configuration values the service will use
// to setup its components.
type Config struct {
	// Shared configuration used by AWS clients
	AwsConfig *aws.Config
	// SQS queue URL job messages will be available at
	WorkerQueueURL string
	// SQS queue URL job results will be written to
	ResultQueueURL string
	// The amount of time in seconds a read job message from the SQS will be
	// hidden from other readers of the queue.
	MessageVisibilityTimeout int64

	// Debug implies we are running in a developer context.
	// it specifically does a handful of things:
	// * continuously loops around looking for new messages
	// * Emits lots of pretty debug messages
	// * Waits around for things to finish
	Debug bool
}

func GetConfig() (Config, error) {

	// Make sure we can get the AWS configuration. This shouldn't fail, but maybe it can.
	// Make sure that we can.
	ourAwsConfig, err := awsConfig.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatal("Couldn't load config...")
		return Config{}, err
	}

	// This is the actual configuration structure we're going to work with
	// we can pre-fill some information ahead of time.
	c := Config{
		WorkerQueueURL: os.Getenv("WORKER_QUEUE_URL"),
		ResultQueueURL: os.Getenv("WORKER_RESULT_QUEUE_URL"),
		AwsConfig:      &ourAwsConfig,
	}

	// double check our values to make sure we have something non-empty.
	if c.WorkerQueueURL == "" {
		return c, fmt.Errorf("missing WORKER_QUEUE_URL")
	}
	if c.ResultQueueURL == "" {
		return c, fmt.Errorf("missing WORKER_RESULT_QUEUE_URL")
	}

	// Double check the region that we're running in.
	if c.AwsConfig.Region == "" {
		// imds gives us information about where we are
		imdsClient := imds.NewFromConfig(*c.AwsConfig, nil)
		// Get the region ...
		regionInfo, err := imdsClient.GetRegion(context.TODO(), &imds.GetRegionInput{})
		if err != nil {
			log.Fatal("Couldn't get EC2 metadata based region", zap.Error(err))
		}
		// ... and set our configuration's region to the region we're
		// actually in so that there's no ambiguity.
		c.AwsConfig.Region = regionInfo.Region
	}

	// Timeouts to apply to the messages while counting words.
	if timeoutStr := os.Getenv("WORKER_MESSAGE_VISIBILITY"); timeoutStr != "" {
		timeout, err := strconv.ParseInt(timeoutStr, 10, 64)
		if err != nil {
			return c, err
		}
		if timeout <= 0 {
			return c, fmt.Errorf("invalid message visibility timeout")
		}
		c.MessageVisibilityTimeout = timeout
	} else {
		c.MessageVisibilityTimeout = defaultMessageVisibilityTimeout
	}

	// Debug?
	c.Debug = false

	debugValue, debugExists := os.LookupEnv("WORKER_DEBUG")
	if debugExists {
		c.Debug, err = strconv.ParseBool(debugValue)
		if err != nil {
			log.Error("failed to convert debug, defaulting to true", zap.String("unparsedValue", debugValue))
			c.Debug = true
		}
	}

	return c, nil
}
