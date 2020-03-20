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

    "github.com/aws/aws-sdk-go/aws/session"
)

// Config represents the information for the test
type Config struct {
    LambdaARN string `json:"LambdaARN"`
    RoleARN   string `json:"RoleARN"`
    RoleName  string `json:"RoleName"`
    RuleName  string `json:"RuleName"`
    Schedule  string `json:"Schedule"`
}

var configFileName = "config.json"

var globalConfig Config

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

    if globalConfig.RoleName == "" {
        globalConfig.RoleName = "TestRole"
    }

    if globalConfig.Schedule == "" {
        globalConfig.Schedule = "rate(5 minutes)"
    }

    t.Log("LambdaARN: " + globalConfig.LambdaARN)
    t.Log("RoleARN:   " + globalConfig.RoleARN)
    t.Log("RoleName:  " + globalConfig.RoleName)
    t.Log("RuleName:  " + globalConfig.RuleName)
    t.Log("Schedule:  " + globalConfig.Schedule)

    if globalConfig.LambdaARN == "" {
        return errors.New("No Lambda ARN was configured")
    }

    return nil
}

func TestCreateEvent(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfig(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create role
    //     CreateRole(sess *session.Session, roleName *string) (*iam.CreateRoleOutput, error)
    roleResult, err := CreateRole(sess, &globalConfig.RoleName)
    if err != nil {
        t.Fatal(err)
    }

    globalConfig.RoleARN = *roleResult.Role.Arn

    // Create rule
    //     CreateRule(sess *session.Session, ruleName *string, roleARN *string, schedule *string) (*cloudwatchevents.PutRuleOutput, error)
    _, err = CreateRule(sess, &globalConfig.RuleName, &globalConfig.RoleARN, &globalConfig.Schedule)
    if err != nil {
        t.Fatal(err)
    }

    // ruleARN := ruleResult.RuleArn

    // Create target
    //     CreateTarget(sess *session.Session, rule *string, lambdaARN *string, targetID *string) (*cloudwatchevents.PutTargetsOutput, error)
    _, err = CreateTarget(sess, &globalConfig.RuleName, &globalConfig.LambdaARN, &globalConfig.RoleName)
    if err != nil {
        t.Fatal(err)
    }

    event, err := getEventInfo()
    if err != nil {
        fmt.Println("Could not get event info from " + eventFile)
        return
    }

    err = CreateEvent(sess, &globalConfig.LambdaARN, event)
    if err != nil {
        fmt.Println("Could not create event")
        return
    }

    fmt.Println("Created event")
}
