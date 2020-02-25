/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package main

import (
    "encoding/json"
    "fmt"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
    "github.com/google/uuid"
)

type config struct {
    InstanceName string `json:"InstanceName"`
    InstanceID   string `json:"InstanceID"`
}

var globalConfig config

var configFileName = "config.json"

func populateConfig(configFile string) error {
    // Get and store configuration values
    // Get configuration from config.json

    // Get entire file as a JSON string
    content, err := ioutil.ReadFile(configFile)
    if err != nil {
        return err
    }

    // Convert []byte to string
    text := string(content)

    // Marshall JSON string in text into global struct
    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    return nil
}

func enableAlarm(instanceName string, instanceID string, alarmName string) error {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    // snippet-start:[cloudwatch.go.enable_alarm.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new CloudWatch client
    svc := cloudwatch.New(sess)
    // snippet-end:[cloudwatch.go.enable_alarm.session]

    // snippet-start:[cloudwatch.go.enable_alarm.put]
    // Get region for alarm action
    region := svc.Config.Region

    _, err := svc.PutMetricAlarm(&cloudwatch.PutMetricAlarmInput{
        AlarmName:          aws.String(alarmName),
        ComparisonOperator: aws.String(cloudwatch.ComparisonOperatorGreaterThanThreshold),
        EvaluationPeriods:  aws.Int64(1),
        MetricName:         aws.String("CPUUtilization"),
        Namespace:          aws.String("AWS/EC2"),
        Period:             aws.Int64(60),
        Statistic:          aws.String(cloudwatch.StatisticAverage),
        Threshold:          aws.Float64(70.0),
        ActionsEnabled:     aws.Bool(true),
        AlarmDescription:   aws.String("Alarm when server CPU exceeds 70%"),
        Unit:               aws.String(cloudwatch.StandardUnitSeconds),
        AlarmActions: []*string{
            aws.String(fmt.Sprintf("arn:aws:swf:"+*region+":%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", instanceName)),
        },
        Dimensions: []*cloudwatch.Dimension{
            {
                Name:  aws.String("InstanceId"),
                Value: aws.String(instanceID),
            },
        },
    })
    // snippet-end:[cloudwatch.go.enable_alarm.put]
    if err != nil {
        return err
    }

    // Enable the alarm for the instance
    // snippet-start:[cloudwatch.go.enable_alarm.enable]
    _, err = svc.EnableAlarmActions(&cloudwatch.EnableAlarmActionsInput{
        AlarmNames: []*string{
            aws.String(instanceID),
        },
    })
    // snippet-end:[cloudwatch.go.enable_alarm.enable]
    if err != nil {
        return err
    }

    return nil
}

func TestDisableAlarmActions(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Started unit test at " + nowString)

    // Get configuration values
    err := populateConfig(configFileName)
    if err != nil {
        msg := "Could not get configuration values from " + configFileName
        t.Fatal(msg)
    }

    if globalConfig.InstanceName == "" || globalConfig.InstanceID == "" {
        msg := "You must supply an instance name and instance ID in " + configFileName
        t.Fatal(msg)
    }

    // Create random alarm name
    id := uuid.New()
    alarmName := "Alarm70-" + id.String()

    t.Log("Instance Name: " + globalConfig.InstanceName)
    t.Log("Instance ID:   " + globalConfig.InstanceID)
    t.Log("Alarm name:    " + alarmName)

    // Create an alarm to disable
    err = enableAlarm(globalConfig.InstanceName, globalConfig.InstanceID, alarmName)
    if err != nil {
        msg := "Could not create alarm " + alarmName + " for instance " + globalConfig.InstanceName
        t.Fatal(msg)
    }

    t.Log("Enabled alarm " + alarmName + " for instance " + globalConfig.InstanceName)

    // Disable alarm
    err = DisableAlarm(alarmName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Disabled alarm " + alarmName)
}
