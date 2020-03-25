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
    "io/ioutil"
    "strconv"
    "testing"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatchlogs"
)

type Config struct {
    Limit         int64  `json:"Limit"`
    LogGroupName  string `json:"LogGroupName"`
    LogStreamName string `json:"LogStreamName"`
}

var configFileName = "config.json"

var globalConfig Config

func getLogGroupStreamNames(sess *session.Session, groupIndex int) (string, string, error) {
    svc := cloudwatchlogs.New(sess)

    resp, err := svc.DescribeLogGroups(nil)
    if err != nil {
        return "", "", err
    }

    if groupIndex < len(resp.LogGroups) {
        logGroupName := resp.LogGroups[groupIndex].LogGroupName

        result, err := svc.DescribeLogStreams(&cloudwatchlogs.DescribeLogStreamsInput{
            LogGroupName: logGroupName,
        })
        if err != nil {
            return "", "", err
        }

        return *logGroupName, *result.LogStreams[0].LogStreamName, nil
    }

    return "", "", nil
}

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

    t.Log("Limit:         " + strconv.Itoa(int(globalConfig.Limit)))
    t.Log("LogGroupName:  " + globalConfig.LogGroupName)
    t.Log("LogStreamName: " + globalConfig.LogStreamName)

    return nil
}

func TestGetLogEvents(t *testing.T) {
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.Limit < 1 {
        globalConfig.Limit = 1
    }

    if globalConfig.Limit > 100 {
        globalConfig.Limit = 100
    }

    logGroupIndex := 0
    gotEvents := false
    var Resp *cloudwatchlogs.GetLogEventsOutput

    for !gotEvents {
        globalConfig.LogGroupName, globalConfig.LogStreamName, err = getLogGroupStreamNames(sess, logGroupIndex)
        if err != nil {
            t.Fatal(err)
        }

        Resp, err = GetLogEvents(sess, &globalConfig.Limit, &globalConfig.LogGroupName, &globalConfig.LogStreamName)
        if err != nil {
            t.Fatal(err)
        }

        length := len(Resp.Events)

        if length > 0 {
            gotEvents = true
        } else {
            globalConfig.LogGroupName = ""
            globalConfig.LogStreamName = ""
            gotEvents = false
            logGroupIndex++
        }
    }

    t.Log("Event messages for stream " + globalConfig.LogStreamName + " in log group " + globalConfig.LogGroupName)

    for _, event := range Resp.Events {
        if nil != event.Message {
            t.Log("  ", *event.Message)
        }
    }

    t.Log("Got " + strconv.Itoa(len(Resp.Events)) + " event(s) for log stream " + globalConfig.LogStreamName + " in log group " + globalConfig.LogGroupName)
}
