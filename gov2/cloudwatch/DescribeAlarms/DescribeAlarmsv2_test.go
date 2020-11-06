package main

import (
	"context"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch"
	"github.com/aws/aws-sdk-go-v2/service/cloudwatch/types"
)

type CWDescribeAlarmsImpl struct{}

func (dt CWDescribeAlarmsImpl) DescribeAlarms(ctx context.Context,
	params *cloudwatch.DescribeAlarmsInput,
	optFns ...func(*cloudwatch.Options)) (*cloudwatch.DescribeAlarmsOutput, error) {
	// Create two dummy composite alarms
	composites := make([]*types.CompositeAlarm, 2)
	composites[0] = &types.CompositeAlarm{AlarmName: aws.String("dummycompositealarm1")}
	composites[1] = &types.CompositeAlarm{AlarmName: aws.String("dummycompositealarm2")}

	// Create two dummy metric alarms
	metrics := make([]*types.MetricAlarm, 2)
	metrics[0] = &types.MetricAlarm{AlarmName: aws.String("dummymetricalarm1")}
	metrics[1] = &types.MetricAlarm{AlarmName: aws.String("dummymetricalarm2")}

	output := &cloudwatch.DescribeAlarmsOutput{
		CompositeAlarms: composites,
		MetricAlarms:    metrics,
	}

	return output, nil
}

func TestDescribeAlarms(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	// Build the request with its input parameters
	input := cloudwatch.DescribeAlarmsInput{}

	api := &CWDescribeAlarmsImpl{}

	resp, err := ListAlarms(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Composite alarms:")
	for _, alarm := range resp.CompositeAlarms {
		t.Log("    " + *alarm.AlarmName)
	}

	t.Log("Metric alarms:")
	for _, alarm := range resp.MetricAlarms {
		t.Log("    " + *alarm.AlarmName)
	}
}
