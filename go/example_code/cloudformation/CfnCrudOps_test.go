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

    "github.com/aws/aws-sdk-go/aws/session"
)

// Config stores our global configuration values (replace env values with these)
type Config struct {
    TemplateFile    string `json:"TemplateFile"`
    MaxRetrySeconds int    `json:"MaxRetrySeconds"`
    Debug           bool   `json:"Debug"`
}

var configFileName = "config.json"

// Gloval variable for configuration set in config.json
var globalConfig Config

func multiplyDuration(factor int64, d time.Duration) time.Duration {
    return time.Duration(factor) * d
}

func PopulateConfiguration(t *testing.T) error {
    // Get and store configuration values from config.json

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

    // Set minimum duration for trying to get results
    if globalConfig.MaxRetrySeconds < 10 {
        globalConfig.MaxRetrySeconds = 10
    }

    return nil
}

func TestCfnCrudOps(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Started unit test at " + nowString)

    err := PopulateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file (~/.aws/credentials)
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create a unique GUID for stack name
    id := uuid.New()
    stackName := "stack-" + id.String()

    // Create stack
    err = CreateStack(sess, stackName, globalConfig.TemplateFile)
    if err != nil {
        t.Fatal(err)
    }

    // Double-check that stack is created
    var duration int64
    duration = 1
    foundStack := false

    for int(duration) < globalConfig.MaxRetrySeconds {
        stacks, err := GetStackSummaries(sess)
        if err != nil {
            t.Fatal(err)
        }

        // Is stackName in list and the status CREATE_COMPLETE?
        for _, s := range stacks {
            if *s.StackName == stackName && *s.StackStatus == "CREATE_COMPLETE" {
                t.Log(stackName + " was successfully created")
                foundStack = true
                break
            }
        }

        if foundStack {
            break
        }

        ts := multiplyDuration(duration, time.Second)
        time.Sleep(ts)

        duration = duration * 2
    }

    if !foundStack {
        t.Fatal("Could not verify " + stackName + " was created")
    }

    // Delete stack
    err = DeleteStack(sess, stackName)
    if err != nil {
        t.Fatal(err)
    }

    // Double-check that stack is deleted
    duration = 1
    foundStack = false

    for int(duration) < globalConfig.MaxRetrySeconds {
        stacks, err := GetStackSummaries(sess)
        if err != nil {
            t.Fatal(err)
        }

        // Is stackName in list?
        // If so, is it's status CREATE_COMPLETE?
        for _, s := range stacks {
            if *s.StackName == stackName && *s.StackStatus == "DELETE_COMPLETE" {
                t.Log(stackName + " was successfully deleted")
                foundStack = true
                break
            }
        }

        if foundStack {
            break
        }

        ts := multiplyDuration(duration, time.Second)
        time.Sleep(ts)

        duration = duration * 2
    }

    if !foundStack {
        t.Fatal("Could not verify " + stackName + " was deleted")
    }
}
