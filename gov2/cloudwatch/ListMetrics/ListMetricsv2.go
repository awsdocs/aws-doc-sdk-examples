// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[cloudwatch.go-v2.ListMetrics]
package main

import (
	"context"
	"fmt"
	"strconv"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
)

// CWListMetricsAPI defines the interface for the ListMetrics function.
// We use this interface to test the function using a mocked service.
type CWListMetricsAPI interface {
	ListMetrics(ctx context.Context,
		params *cloudwatch.ListMetricsInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.ListMetricsOutput, error)
}

// GetMetrics gets the name, namespace, and dimension name of your Amazon CloudWatch metrics
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a ListMetricsOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to ListMetrics
func GetMetrics(c context.Context, api CWListMetricsAPI, input *cloudwatch.ListMetricsInput) (*cloudwatch.ListMetricsOutput, error) {
	result, err := api.ListMetrics(c, input)

	return result, err
}

func main() {
	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	input := &cloudwatch.ListMetricsInput{}

	result, err := GetMetrics(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Could not get metrics")
		return
	}

	fmt.Println("Metrics:")
	numMetrics := 0

	for _, m := range result.Metrics {
		fmt.Println("   Metric Name: " + *m.MetricName)
		fmt.Println("   Namespace:   " + *m.Namespace)
		fmt.Println("   Dimensions:")
		for _, d := range m.Dimensions {
			fmt.Println("      " + *d.Name + ": " + *d.Value)
		}

		fmt.Println("")
		numMetrics++
	}

	fmt.Println("Found " + strconv.Itoa(numMetrics) + " metrics")
}

// snippet-end:[cloudwatch.go-v2.ListMetrics]
