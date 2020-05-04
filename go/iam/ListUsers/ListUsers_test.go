// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. // SPDX-License-Identifier: MIT-0

package main

import (
    "encoding/json"
    "io/ioutil"
    "strconv"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go/aws/session"
)

type Config struct {
    MaxUsers int64 `json:"MaxUsers"`
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

    t.Log("MaxUsers: " + strconv.Itoa(int(globalConfig.MaxUsers)))

    return nil
}

func TestListUsers(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.MaxUsers < int64(0) {
        globalConfig.MaxUsers = int64(10)
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    maxUsers := int64(10)

    _, err = GetUsers(sess, &maxUsers)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Retrieved list of users")
}
