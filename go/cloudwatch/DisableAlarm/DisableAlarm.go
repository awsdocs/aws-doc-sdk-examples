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
// snippet-start:[cloudwatch.go.disable_alarm]
package main

// snippet-start:[cloudwatch.go.disable_alarm.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
)
// snippet-end:[cloudwatch.go.disable_alarm.imports]

// DisableAlarm disables an alarm
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     alarmName is the name of the alarm
// Output:
//     If successful, nil
//     Otherwise, the error from a call to DisableAlarmActions
func DisableAlarm(sess *session.Session, alarmName *string) error {
    // Create new CloudWatch client.
    // snippet-start:[cloudwatch.go.disable_call]
    svc := cloudwatch.New(sess)

    _, err := svc.DisableAlarmActions(&cloudwatch.DisableAlarmActionsInput{
        AlarmNames: []*string{
            alarmName,
        },
    })
    // snippet-end:[cloudwatch.go.disable_call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[cloudwatch.go.disable_args]
    alarmName := flag.String("a", "", "The name of the alarm to disable")
    flag.Parse()

    if *alarmName == "" {
        fmt.Println("You must supply an alarm name to disable")
        return
    }
    // snippet-end:[cloudwatch.go.disable_args]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    // snippet-start:[cloudwatch.go.disable_session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudwatch.go.disable_session]

    err := DisableAlarm(sess, alarmName)
    if err != nil {
        fmt.Println("Could not disable alarm " + *alarmName)
    } else {
        fmt.Println("Disabled alarm " + *alarmName)
    }
}
// snippet-end:[cloudwatch.go.disable_alarm]
