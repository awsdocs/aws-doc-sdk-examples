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

    "github.com/google/uuid"
)

type config struct {
	InstanceName string `json:"InstanceName"`
	InstanceID   string `json:"InstanceID"`
	AlarmName    string `json:"AlarmName"`
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

        if globalConfig.AlarmName == "" {
            // Create random alarm name
            id := uuid.New()
            globalConfig.AlarmName := "Alarm70-" + id.String()
        }
        
}

func TestEnableAlarm(t *testing.T) {
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

	if globalConfig.InstanceName == "" || globalConfig.InstanceID == "" || globalConfig.AlarmName == "" {
		msg := "You must supply an instance name, instance ID, and alarm name in " + configFileName
		t.Fatal(msg)
	}

	t.Log("Instance name: " + globalConfig.InstanceName)
	t.Log("Instance ID:   " + globalConfig.InstanceID)
	t.Log("Alarm name:    " + globalConfig.AlarmName)

	// Enable alarm
	err = EnableAlarm(globalConfig.InstanceName, globalConfig.InstanceID, globalConfig.AlarmName)
	if err != nil {
		t.Fatal(err)
	}

	t.Log("Enabled alarm " + globalConfig.AlarmName + " for instance " + globalConfig.InstanceName)
}
