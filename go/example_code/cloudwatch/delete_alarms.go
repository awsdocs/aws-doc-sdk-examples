sess, err := session.NewSession()
if err != nil {
    fmt.Println("failed to create session,", err)
    return
}

svc := cloudwatch.New(sess)

params := &cloudwatch.DeleteAlarmsInput{
    AlarmNames: []*string{ // Required
        aws.String("AlarmName"), // Required
        // More values...
    },
}
resp, err := svc.DeleteAlarms(params)

if err != nil {
    // Print the error, cast err to awserr.Error to get the Code and
    // Message from an error.
    fmt.Println(err.Error())
    return
}

// Pretty-print the response data.
fmt.Println(resp)