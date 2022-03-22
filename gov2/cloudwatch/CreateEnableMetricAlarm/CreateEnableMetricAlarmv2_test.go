package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

type CWEnableAlarmImpl struct{}

func (dt CWEnableAlarmImpl) PutMetricAlarm(ctx context.Context,
	params *cloudwatch.PutMetricAlarmInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.PutMetricAlarmOutput, error) {
	return &cloudwatch.PutMetricAlarmOutput{}, nil
}

func (dt CWEnableAlarmImpl) EnableAlarmActions(ctx context.Context,
	params *cloudwatch.EnableAlarmActionsInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.EnableAlarmActionsOutput, error) {
	return &cloudwatch.EnableAlarmActionsOutput{}, nil
}

type Config struct {
	InstanceName string `json:"InstanceName"`
	InstanceID   string `json:"InstanceID"`
	AlarmName    string `json:"AlarmName"`
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

	if globalConfig.InstanceName == "" || globalConfig.InstanceID == "" || globalConfig.AlarmName == "" {
		msg := "You must specify a value for InstanceName, InstanceID, and AlarmName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestEnableAlarm(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	cfg, err := config.LoadDefaultConfig(context.Background())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	api := &CWEnableAlarmImpl{}

	putInput := &cloudwatch.PutMetricAlarmInput{
		AlarmName:          &globalConfig.AlarmName,
		ComparisonOperator: types.ComparisonOperatorGreaterThanOrEqualToThreshold,
		EvaluationPeriods:  aws.Int32(1),
		MetricName:         aws.String("CPUUtilization"),
		Namespace:          aws.String("AWS/EC2"),
		Period:             aws.Int32(60),
		Statistic:          types.StatisticAverage,
		Threshold:          aws.Float64(70.0),
		ActionsEnabled:     aws.Bool(true),
		AlarmDescription:   aws.String("Alarm when server CPU exceeds 70%"),
		Unit:               types.StandardUnitSeconds,
		AlarmActions: []string{
			fmt.Sprintf("arn:aws:swf:"+cfg.Region+":%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", globalConfig.InstanceName),
		},
		Dimensions: []types.Dimension{
			{
				Name:  aws.String("InstanceId"),
				Value: &globalConfig.InstanceID,
			},
		},
	}

	_, err = CreateMetricAlarm(context.Background(), *api, putInput)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	enableInput := &cloudwatch.EnableAlarmActionsInput{
		AlarmNames: []string{
			globalConfig.InstanceID,
		},
	}

	_, err = EnableAlarm(context.Background(), *api, enableInput)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Enabled alarm " + globalConfig.AlarmName + " for EC2 instance " + globalConfig.InstanceName)
}
