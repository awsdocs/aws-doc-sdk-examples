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
    "errors"
    "fmt"
    "io/ioutil"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/google/uuid"
)

type config struct {
    InstanceName string `json:"InstanceName"`
    InstanceID   string `json:"InstanceID"`
    AlarmName    string `json:"AlarmName"`
}

var globalConfig config

var configFileName = "config.json"

func getEc2Info(sess *session.Session, t *testing.T) (string, string, error) {
    svc := ec2.New(sess)

    result, err := svc.DescribeInstances(nil)
    if err != nil {
        return "", "", err
    }

    for _, reservation := range result.Reservations {
        for _, instance := range reservation.Instances {
            id := instance.InstanceId
            name := ""

            for _, tag := range instance.Tags {
                if *tag.Key == "Name" {
                    name = *tag.Value
                    break
                }
            }

            if *id != "" && name != "" {
                return *id, name, nil
            }
        }
    }

    return "", "", errors.New("No EC2 instance found with name and ID")
}

func populateConfig(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    t.Log("Instance Name: " + globalConfig.InstanceName)
    t.Log("Instance ID:   " + globalConfig.InstanceID)
    t.Log("Alarm name:    " + globalConfig.AlarmName)

    return nil
}

func createAlarm(sess *session.Session, instanceName *string, instanceID *string, alarmName *string) error {
    svc := cloudwatch.New(sess)

    _, err := svc.PutMetricAlarm(&cloudwatch.PutMetricAlarmInput{
        AlarmName:          alarmName,
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
            aws.String(fmt.Sprintf("arn:aws:swf:"+*svc.Config.Region+":%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", *instanceName)),
        },
        Dimensions: []*cloudwatch.Dimension{
            {
                Name:  aws.String("InstanceId"),
                Value: instanceID,
            },
        },
    })
    if err != nil {
        return err
    }

    return nil
}

func TestDisableAlarmActions(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Started unit test at " + nowString)


    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    err := populateConfig(sess, t)
    if err != nil {
        msg := "Could not get configuration values from " + configFileName
        t.Fatal(msg)
    }

    alarmCreated := false

    if globalConfig.AlarmName == "" {
        if globalConfig.AlarmName == "" {
            id := uuid.New()
            globalConfig.AlarmName = "Alarm70-" + id.String()
        }

        if globalConfig.InstanceName == "" || globalConfig.InstanceID == "" {
            globalConfig.InstanceName, globalConfig.InstanceID, err = getEc2Info(sess, t)
            if err != nil {
                return err
            }
        }

        err = enableAlarm(sess, &globalConfig.InstanceName, &globalConfig.InstanceID, &globalConfig.AlarmName)
        if err != nil {
            msg := "Could not create alarm " + globalConfig.AlarmName + " for instance " + globalConfig.InstanceName
            t.Fatal(msg)
        }

        t.Log("Enabled alarm " + globalConfig.AlarmName + " for instance " + globalConfig.InstanceName)

        err = disableAlarm(sess, &globalConfig.AlarmName)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Disabled alarm " + globalConfig.AlarmName)
    }
    
    err = DeleteAlarm(sess, &globalConfig.AlarmName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Deleted alarm " + globalConfig.AlarmName)
}
