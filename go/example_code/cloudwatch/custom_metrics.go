 
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
)

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new cloudwatch client.
    svc := cloudwatch.New(sess)

    _, err := svc.PutMetricData(&cloudwatch.PutMetricDataInput{
        Namespace: aws.String("Site/Traffic"),
        MetricData: []*cloudwatch.MetricDatum{
            &cloudwatch.MetricDatum{
                MetricName: aws.String("UniqueVisitors"),
                Unit:       aws.String("Count"),
                Value:      aws.Float64(5885.0),
                Dimensions: []*cloudwatch.Dimension{
                    &cloudwatch.Dimension{
                        Name:  aws.String("SiteName"),
                        Value: aws.String("example.com"),
                    },
                },
            },
            &cloudwatch.MetricDatum{
                MetricName: aws.String("UniqueVisits"),
                Unit:       aws.String("Count"),
                Value:      aws.Float64(8628.0),
                Dimensions: []*cloudwatch.Dimension{
                    &cloudwatch.Dimension{
                        Name:  aws.String("SiteName"),
                        Value: aws.String("example.com"),
                    },
                },
            },
            &cloudwatch.MetricDatum{
                MetricName: aws.String("PageViews"),
                Unit:       aws.String("Count"),
                Value:      aws.Float64(18057.0),
                Dimensions: []*cloudwatch.Dimension{
                    &cloudwatch.Dimension{
                        Name:  aws.String("PageURL"),
                        Value: aws.String("my-page.html"),
                    },
                },
            },
        },
    })
    if err != nil {
        fmt.Println("Error adding metrics:", err.Error())
        return
    }

    // Get information about metrics
    result, err := svc.ListMetrics(&cloudwatch.ListMetricsInput{
        Namespace: aws.String("Site/Traffic"),
    })
    if err != nil {
        fmt.Println("Error getting metrics:", err.Error())
        return
    }

    for _, metric := range result.Metrics {
        fmt.Println(*metric.MetricName)

        for _, dim := range metric.Dimensions {
            fmt.Println(*dim.Name + ":", *dim.Value)
            fmt.Println()
        }
    }
}
