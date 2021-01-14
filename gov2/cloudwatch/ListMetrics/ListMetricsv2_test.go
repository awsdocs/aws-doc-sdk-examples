// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package main

import (
	"context"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

type CWListMetricsImpl struct{}

func (dt CWListMetricsImpl) ListMetrics(ctx context.Context,
	params *cloudwatch.ListMetricsInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.ListMetricsOutput, error) {

	// Create a list of two dummy metrics
	metrics := make([]*types.Metric, 2)
	metrics[0] = &types.Metric{}
	metrics[1] = &types.Metric{}

	output := &cloudwatch.ListMetricsOutput{
		Metrics: metrics,
	}

	return output, nil
}

func TestListMetrics(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// Build the request with its input parameters
	input := cloudwatch.ListMetricsInput{}

	api := &CWListMetricsImpl{}

	resp, err := GetMetrics(context.TODO(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Found " + strconv.Itoa(len(resp.Metrics)) + " metrics")
}
