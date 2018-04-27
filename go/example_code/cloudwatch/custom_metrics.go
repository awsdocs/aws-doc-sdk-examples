/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"

    "fmt"
    "os"
)

func main() {
    if len(os.Args) != 4 {
        fmt.Println("You must supply a metric name, dimension, value, and namespace")
        os.Exit(1)
    }

    metric := os.Args[1]
    dimension := os.Args[3]
    namespace := os.Args[2]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new cloudwatch client.
    svc := cloudwatch.New(sess)

    // This will disable the alarm.
    result, err := svc.PutMetricData(&cloudwatch.PutMetricDataInput{
        MetricData: []*cloudwatch.MetricDatum{
            &cloudwatch.MetricDatum{
                MetricName: aws.String(metric),
                Unit:       aws.String(cloudwatch.StandardUnitNone),
                Value:      aws.Float64(1.0),
                Dimensions: []*cloudwatch.Dimension{
                    &cloudwatch.Dimension{
                        Name:  aws.String("dimension"),
                        Value: aws.String(dimension),
                    },
                },
            },
        },
        Namespace: aws.String(namespace),
    })
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Success", result)
}
