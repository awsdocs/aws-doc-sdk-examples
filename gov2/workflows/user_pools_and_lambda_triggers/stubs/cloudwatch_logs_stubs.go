// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"fmt"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatchlogs/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubDescribeLogStreams(lambdaName string, streamName string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DescribeLogStreams",
		Input: &cloudwatchlogs.DescribeLogStreamsInput{
			Descending:   aws.Bool(true),
			Limit:        aws.Int32(1),
			LogGroupName: aws.String(fmt.Sprintf("/aws/lambda/%s", lambdaName)),
			OrderBy:      types.OrderByLastEventTime,
		},
		Output: &cloudwatchlogs.DescribeLogStreamsOutput{
			LogStreams: []types.LogStream{{LogStreamName: aws.String(streamName)}},
		},
		Error: raiseErr,
	}
}

func StubGetLogEvents(lambdaName string, streamName string, eventCount int32, eventMsgs []string, raiseErr *testtools.StubError) testtools.Stub {
	outEvents := make([]types.OutputLogEvent, len(eventMsgs))
	for i, msg := range eventMsgs {
		outEvents[i] = types.OutputLogEvent{Message: aws.String(msg)}
	}
	return testtools.Stub{
		OperationName: "GetLogEvents",
		Input: &cloudwatchlogs.GetLogEventsInput{
			LogStreamName: aws.String(streamName),
			Limit:         aws.Int32(eventCount),
			LogGroupName:  aws.String(fmt.Sprintf("/aws/lambda/%s", lambdaName)),
		},
		Output: &cloudwatchlogs.GetLogEventsOutput{Events: outEvents},
		Error:  raiseErr,
	}
}
