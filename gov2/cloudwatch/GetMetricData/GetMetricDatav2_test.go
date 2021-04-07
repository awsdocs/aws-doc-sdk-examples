package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

type CWGetMetricDataImpl struct{}

func (dt CWGetMetricDataImpl) GetMetricData(ctx context.Context,
	params *cloudwatch.GetMetricDataInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.GetMetricDataOutput, error) {
	return &cloudwatch.GetMetricDataOutput{}, nil
}

type Config struct {
	Namespace      string `json:"Namespace"`
	MetricName     string `json:"MetricName"`
	DimensionName  string `json:"DimensionName"`
	DimensionValue string `json:"DimensionValue"`
	ID             string `json:"Id"`
	DiffInMinutes  int    `json:"DiffInMinutes"`
	Stat           string `json:"stat"`
	Period         int    `json:"Period"`
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

	if globalConfig.Namespace == "" || globalConfig.MetricName == "" || globalConfig.ID == "" || globalConfig.DimensionName == "" || globalConfig.DimensionValue == "" || globalConfig.DiffInMinutes == 0 || globalConfig.Period == 0 || globalConfig.Stat == "" {
		msg := "You must supply metricName, namespace, dimensionName, dimensionValue, id, diffInMinutes, stat, period in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestGetMetricData(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := &cloudwatch.GetMetricDataInput{
		EndTime:   aws.Time(time.Unix(time.Now().Unix(), 0)),
		StartTime: aws.Time(time.Unix(time.Now().Add(time.Duration(-globalConfig.DiffInMinutes)*time.Minute).Unix(), 0)),
		MetricDataQueries: []types.MetricDataQuery{
			types.MetricDataQuery{
				Id: aws.String(globalConfig.ID),
				MetricStat: &types.MetricStat{
					Metric: &types.Metric{
						Namespace:  aws.String(globalConfig.Namespace),
						MetricName: aws.String(globalConfig.MetricName),
						Dimensions: []types.Dimension{
							types.Dimension{
								Name:  aws.String(globalConfig.DimensionName),
								Value: aws.String(globalConfig.DimensionValue),
							},
						},
					},
					Period: aws.Int32(int32(globalConfig.Period)),
					Stat:   aws.String(globalConfig.Stat),
				},
			},
		},
	}

	api := &CWGetMetricDataImpl{}

	_, err = GetMetrics(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Fetched Metric Data")
}
