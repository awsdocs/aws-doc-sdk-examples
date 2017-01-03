/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
