// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

type CWPutMetricDataImpl struct{}

func (dt CWPutMetricDataImpl) PutMetricData(ctx context.Context,
	params *cloudwatch.PutMetricDataInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.PutMetricDataOutput, error) {
	return &cloudwatch.PutMetricDataOutput{}, nil
}

type Config struct {
	Namespace      string `json:"Namespace"`
	MetricName     string `json:"MetricName"`
	MetricValueS   string `json:"MetricValue"`
	MetricValue    float64
	DimensionName  string `json:"DimensionName"`
	DimensionValue string `json:"DimensionValue"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.Namespace == "" || globalConfig.MetricName == "" || globalConfig.MetricValueS == "" || globalConfig.DimensionName == "" || globalConfig.DimensionValue == "" {
		msg := "You must specify a value for Namespace, MetricName, MetricValue, DimensionName, and DimensionValue in " + configFileName
		return errors.New(msg)
	}

	// Make sure metric value is a float64
	f, err := strconv.ParseFloat(globalConfig.MetricValueS, 64)
	if err != nil {
		return err
	}

	globalConfig.MetricValue = f

	return nil
}

func TestCreateCustomMetric(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := &cloudwatch.PutMetricDataInput{
		Namespace: &globalConfig.Namespace,
		MetricData: []types.MetricDatum{
			{
				MetricName: &globalConfig.MetricName,
				Unit:       types.StandardUnitSeconds,
				Value:      &globalConfig.MetricValue,
				Dimensions: []types.Dimension{
					{
						Name:  &globalConfig.DimensionName,
						Value: &globalConfig.DimensionValue,
					},
				},
			},
		},
	}

	api := &CWPutMetricDataImpl{}

	_, err = CreateCustomMetric(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Created a custom metric")
}
