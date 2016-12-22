sess, err := session.NewSession()
if err != nil {
    fmt.Println("failed to create session,", err)
    return
}

svc := cloudwatch.New(sess)

params := &cloudwatch.DescribeAlarmHistoryInput{
    AlarmName:       aws.String("AlarmName"),
    EndDate:         aws.Time(time.Now()),
    HistoryItemType: aws.String("StateUpdate"),
    MaxRecords:      aws.Int64(1),
    NextToken:       aws.String("NextToken"),
    StartDate:       aws.Time(time.Now()),
}
resp, err := svc.DescribeAlarmHistory(params)

if err != nil {
    // Print the error, cast err to awserr.Error to get the Code and
    // Message from an error.
    fmt.Println(err.Error())
    return
}

//TODO: Show storing NextToken from response 
// Pretty-print the response data.
fmt.Println(resp)