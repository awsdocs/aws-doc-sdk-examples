// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"fmt"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs/types"
)

// snippet-start:[gov2.cloudwatch-logs.CloudWatchLogsActions.complete]

type CloudWatchLogsActions struct {
	CwlClient *cloudwatchlogs.Client
}

// GetLatestLogStream gets the most recent log stream for a Lambda function.
func (actor CloudWatchLogsActions) GetLatestLogStream(functionName string) (types.LogStream, error) {
	var logStream types.LogStream
	logGroupName := fmt.Sprintf("/aws/lambda/%s", functionName)
	output, err := actor.CwlClient.DescribeLogStreams(context.TODO(), &cloudwatchlogs.DescribeLogStreamsInput{
		Descending:   aws.Bool(true),
		Limit:        aws.Int32(1),
		LogGroupName: aws.String(logGroupName),
		OrderBy:      types.OrderByLastEventTime,
	})
	if err != nil {
		log.Printf("Couldn't get log streams for log group %v. Here's why: %v\n", logGroupName, err)
	} else {
		logStream = output.LogStreams[0]
	}
	return logStream, err
}

// GetLogEvents gets the most recent eventCount events from the specified log stream.
func (actor CloudWatchLogsActions) GetLogEvents(functionName string, logStreamName string, eventCount int32) (
	[]types.OutputLogEvent, error) {
	var events []types.OutputLogEvent
	logGroupName := fmt.Sprintf("/aws/lambda/%s", functionName)
	output, err := actor.CwlClient.GetLogEvents(context.TODO(), &cloudwatchlogs.GetLogEventsInput{
		LogStreamName: aws.String(logStreamName),
		Limit:         aws.Int32(eventCount),
		LogGroupName:  aws.String(logGroupName),
	})
	if err != nil {
		log.Printf("Couldn't get log event for log stream %v. Here's why: %v\n", logStreamName, err)
	} else {
		events = output.Events
	}
	return events, err
}

// snippet-end:[gov2.cloudwatch-logs.CloudWatchLogsActions.complete]
