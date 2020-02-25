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
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
)

type config struct {
    Namespace string `json:"Namespace"`
    Metrics   []struct {
        MetricName     string  `json:"MetricName"`
        Unit           string  `json:"Unit"`
        Value          float64 `json:"Value"`
        DimensionName  string  `json:"DimensionName"`
        DimensionValue string  `json:"DimensionValue"`
    } `json:"Metrics"`
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

func listMetrics(t *testing.T, sess *session.Session, namespace string) error {
    svc := cloudwatch.New(sess)

    result, err := svc.ListMetrics(&cloudwatch.ListMetricsInput{
        Namespace: aws.String(namespace),
    })
    if err != nil {
        return err
    }

    numMetrics := 0

    for _, m := range result.Metrics {
        t.Log("   Metric Name: " + *m.MetricName)
        t.Log("   Dimensions:")
        for _, d := range m.Dimensions {
            t.Log("      " + *d.Name + ": " + *d.Value)
        }

        t.Log("")
        numMetrics++
    }

    t.Log("Found " + strconv.Itoa(numMetrics) + " metrics in namespace " + namespace)

    return nil
}

func TestCustomMetric(t *testing.T) {
    // When the test started
    thisTime := time.Now()
    nowString := thisTime.Format("20060102150405")
    t.Log("Started unit test at " + nowString)

    err := populateConfig(configFileName)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    for _, m := range globalConfig.Metrics {
        err := CreateCustomMetric(sess, globalConfig.Namespace, m.MetricName, m.Unit, m.Value, m.DimensionName, m.DimensionValue)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created custom metric " + m.MetricName)
    }

    // Now list them out
    err = listMetrics(t, sess, globalConfig.Namespace)
    if err != nil {
        t.Fatal(err)
    }
}
