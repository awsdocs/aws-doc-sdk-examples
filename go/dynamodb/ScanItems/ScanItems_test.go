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
    Table     string  `json:"Table"`
    MinRating float64 `json:"MinRating"`
    Year      int     `json:"Year"`
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

    t.Log("Table:     " + globalConfig.Table)
    t.Log("Year:      " + strconv.Itoa(globalConfig.Year))
    t.Log("MinRating: " + strconv.Itoa(int(globalConfig.MinRating)))

    return nil
}

func TestScanItems(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := populateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    if globalConfig.Table == "" || globalConfig.Year < 1900 || globalConfig.MinRating < 0.0 {
        t.Fatal("Missing Table name, year < 1900, or MinRating < 0.0 in config.json")
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    result, err := ScanTableItems(sess, &globalConfig.Year, &globalConfig.Table, &globalConfig.MinRating)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Found", strconv.Itoa(len(*result)), "movie(s) with a rating above", globalConfig.MinRating, "in", globalConfig.Year)
}
