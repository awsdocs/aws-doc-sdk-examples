sess, err := session.NewSession()
if err != nil {
    fmt.Println("failed to create session,", err)
    return
}

svc := cloudwatch.New(sess)

params := &cloudwatch.DescribeAlarmsInput{
    ActionPrefix:    aws.String("ActionPrefix"),
    AlarmNamePrefix: aws.String("AlarmNamePrefix"),
    AlarmNames: []*string{
        aws.String("AlarmName"), // Required
        // More values...
    },
    MaxRecords: aws.Int64(1),
    NextToken:  aws.String("NextToken"),
    StateValue: aws.String("ALARM"),
}
resp, err := svc.DescribeAlarms(params)

if err != nil {
    // Print the error, cast err to awserr.Error to get the Code and
    // Message from an error.
    fmt.Println(err.Error())
    return
}

//TODO: show token handling?
// Pretty-print the response data.
fmt.Println(resp)