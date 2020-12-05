// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[cloudwatch.go-v2.CreateCustomMetric]
package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

// CWPutMetricDataAPI defines the interface for the PutMetricData function.
// We use this interface to test the function using a mocked service.
type CWPutMetricDataAPI interface {
	PutMetricData(ctx context.Context,
		params *cloudwatch.PutMetricDataInput,
		optFns ...func(*cloudwatch.Options)) (*cloudwatch.PutMetricDataOutput, error)
}

// CreateCustomMetric creates a new Amazon CloudWatch metric in a namespace
// Inputs:
//     c is the context of the method call, which includes the Region
//     api is the interface that defines the method call
//     input defines the input arguments to the service call.
// Output:
//     If success, a PutMetricDataOutput object containing the result of the service call and nil
//     Otherwise, nil and an error from the call to PutMetricData
func CreateCustomMetric(c context.Context, api CWPutMetricDataAPI, input *cloudwatch.PutMetricDataInput) (*cloudwatch.PutMetricDataOutput, error) {
	resp, err := api.PutMetricData(c, input)

	return resp, err
}

func main() {
	namespace := flag.String("n", "", "The namespace for the metric")
	metricName := flag.String("m", "", "The name of the metric")
	value := flag.Float64("s", 0.0, "The number of seconds for the units")
	dimensionName := flag.String("dn", "", "The name of the dimension")
	dimensionValue := flag.String("dv", "", "The value of the dimension")
	flag.Parse()

	if *namespace == "" || *metricName == "" || *dimensionName == "" || *dimensionValue == "" {
		fmt.Println("You must supply a namespace, metric name, dimension name, and dimension value")
		return
	}

	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	client := cloudwatch.NewFromConfig(cfg)

	input := &cloudwatch.PutMetricDataInput{
		Namespace: namespace,
		MetricData: []*types.MetricDatum{
			&types.MetricDatum{
				MetricName: metricName,
				Unit:       types.StandardUnitSeconds,
				Value:      value,
				Dimensions: []*types.Dimension{
					&types.Dimension{
						Name:  dimensionName,
						Value: dimensionValue,
					},
				},
			},
		},
	}

	_, err = CreateCustomMetric(context.Background(), client, input)
	if err != nil {
		fmt.Println()
		return
	}

	fmt.Println("Created a custom metric")
}

// snippet-end:[cloudwatch.go-v2.CreateCustomMetric]
