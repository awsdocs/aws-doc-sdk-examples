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