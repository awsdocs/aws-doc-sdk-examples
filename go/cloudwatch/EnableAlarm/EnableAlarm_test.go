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
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/google/uuid"
)

type config struct {
    InstanceName string `json:"InstanceName"`
    InstanceID   string `json:"InstanceID"`
    AlarmName    string `json:"AlarmName"`
}

var globalConfig config

var configFileName = "config.json"

func populateConfig(t *testing.T) error {
    // Get and store configuration values
    // Get configuration from config.json

    // Get entire file as a JSON string
    content, err := ioutil.ReadFile(configFileName)
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

    if globalConfig.AlarmName == "" {
        // Create random alarm name
        id := uuid.New()
        globalConfig.AlarmName = "Alarm70-" + id.String()
    }

    t.Log("Instance name: " + globalConfig.InstanceName)
    t.Log("Instance ID:   " + globalConfig.InstanceID)
    t.Log("Alarm name:    " + globalConfig.AlarmName)

    return nil
}

func TestEnableAlarm(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Started unit test at " + nowString)

    // Get configuration values
    err := populateConfig(t)
    if err != nil {
        msg := "Could not get configuration values from " + configFileName
        t.Fatal(msg)
    }

    if globalConfig.InstanceName == "" || globalConfig.InstanceID == "" || globalConfig.AlarmName == "" {
        msg := "You must supply an instance name, instance ID, and alarm name in " + configFileName
        t.Fatal(msg)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Enable alarm
    err = EnableAlarm(sess, &globalConfig.InstanceName, &globalConfig.InstanceID, &globalConfig.AlarmName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Enabled alarm " + globalConfig.AlarmName + " for instance " + globalConfig.InstanceName)
}
