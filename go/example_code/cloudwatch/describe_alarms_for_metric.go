//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Gets the CloudWatch alarms for a specific metric.]
//snippet-keyword:[Amazon CloudWatch]
//snippet-keyword:[DescribeAlarmsForMetric function]
//snippet-keyword:[Go]
//snippet-service:[cloudwatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

sess, err := session.NewSession()
if err != nil {
    fmt.Println("failed to create session,", err)
    return
}

svc := cloudwatch.New(sess)

params := &cloudwatch.DescribeAlarmsForMetricInput{
    MetricName: aws.String("MetricName"), // Required
    Namespace:  aws.String("Namespace"),  // Required
    Dimensions: []*cloudwatch.Dimension{
        { // Required
            Name:  aws.String("DimensionName"),  // Required
            Value: aws.String("DimensionValue"), // Required
        },
        // More values...
    },
    ExtendedStatistic: aws.String("ExtendedStatistic"),
    Period:            aws.Int64(1),
    Statistic:         aws.String("Statistic"),
    Unit:              aws.String("StandardUnit"),
}
resp, err := svc.DescribeAlarmsForMetric(params)

if err != nil {
    // Print the error, cast err to awserr.Error to get the Code and
    // Message from an error.
    fmt.Println(err.Error())
    return
}

// Pretty-print the response data.
fmt.Println(resp)
