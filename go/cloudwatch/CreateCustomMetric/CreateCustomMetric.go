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
// snippet-start: [cloudwatch.go.create_custom_metric]
package main

// snippet-start: [cloudwatch.go.create_custom_metric.import]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
)
// snippet-end: [cloudwatch.go.create_custom_metric.import]

// CreateCustomMetric creates a new metric in a namespace
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     namespace is the metric namespace
//     metricName is the name of the metric
//     unit is what the value represents
//     value is the value of the metric unit
//     dimensionName is the name of the dimension
//     dimensionValue is the value of the dimensionName
// Output:
//     If success, nil
//     Otherwise, an error from a call to PutMetricData
func CreateCustomMetric(sess *session.Session, namespace *string, metricName *string, unit *string, value *float64, dimensionName *string, dimensionValue *string) error {
    // Create new Amazon CloudWatch client
    // snippet-start: [cloudwatch.go.create_custom_metric.call]
    svc := cloudwatch.New(sess)

    _, err := svc.PutMetricData(&cloudwatch.PutMetricDataInput{
        Namespace: namespace,
        MetricData: []*cloudwatch.MetricDatum{
            &cloudwatch.MetricDatum{
                MetricName: metricName,
                Unit:       unit,
                Value:      value,
                Dimensions: []*cloudwatch.Dimension{
                    &cloudwatch.Dimension{
                        Name:  dimensionName,
                        Value: dimensionValue,
                    },
                },
            },
        },
    })
    // snippet-end: [cloudwatch.go.create_custom_metric.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start: [cloudwatch.go.create_custom_metric.args]
    namespace := flag.String("n", "", "The namespace for the metric")
    metricName := flag.String("m", "", "The name of the metric")
    unit := flag.String("u", "", "The units for the metric")
    value := flag.Float64("v", 0.0, "The value of the units")
    dimensionName := flag.String("dn", "", "The name of the dimension")
    dimensionValue := flag.String("dv", "", "The value of the dimension")
    flag.Parse()

    if *namespace == "" || *metricName == "" ||*unit == "" || *dimensionName == "" || *dimensionValue == "" {
        fmt.Println("You must supply a namespace, metric name, value, dimension name, and dimension value")
        return
    }
    // snippet-end: [cloudwatch.go.create_custom_metric.args]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    // snippet-start: [cloudwatch.go.create_custom_metric.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-start: [cloudwatch.go.create_custom_metric.session]
    
    err := CreateCustomMetric(sess, namespace, metricName, unit, value, dimensionName, dimensionValue)
    if err != nil {
        fmt.Println()
    }
}
// snippet-end: [cloudwatch.go.create_custom_metric]
