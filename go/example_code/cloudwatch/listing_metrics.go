 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Go]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
        fmt.Println("You must supply a metric name, namespace, and dimension name")
        os.Exit(1)
    }

    metric := os.Args[1]
    namespace := os.Args[2]
    dimension := os.Args[3]
    
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create CloudWatch client
    svc := cloudwatch.New(sess)

    // Disable the alarm
    result, err := svc.ListMetrics(&cloudwatch.ListMetricsInput{
        MetricName: aws.String(metric),
        Namespace:  aws.String(namespace),
        Dimensions: []*cloudwatch.DimensionFilter{
            &cloudwatch.DimensionFilter{
                Name: aws.String(dimension),
            },
        },
    })
    if err != nil {
        fmt.Println("Error", err)
        return
    }

    fmt.Println("Metrics", result.Metrics)
}
