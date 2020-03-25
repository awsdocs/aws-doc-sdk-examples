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
// snippet-start:[cloudwatch.go.delete_alarm]
package main

// snippet-start:[cloudwatch.go.delete_alarm.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudwatch"
)
// snippet-end:[cloudwatch.go.delete_alarm.imports]

// DeleteAlarm deletes an alarm
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     alarmName is the name of the alarm
// Output:
//     If successful, nil
//     Otherwise, the error from a call to DeleteAlarms
func DeleteAlarm(sess *session.Session, alarmName *string) error {
    // Create service client
    // snippet-start:[cloudwatch.go.delete_alarm.call]
    svc := cloudwatch.New(sess)

    _, err := svc.DeleteAlarms(&cloudwatch.DeleteAlarmsInput{
        AlarmNames: []*string{
            alarmName,
        },
    })
    // snippet-end:[cloudwatch.go.delete_alarm.call]
    if err != nil {
        return err
    }

    return nil
}

func main() {
    // snippet-start:[cloudwatch.go.delete_alarm.args]
    alarmName := flag.String("a", "", "The name of the alarm to delete")
    flag.Parse()

    if *alarmName == "" {
        fmt.Println("You must supply an alarm name to disable")
        return
    }
    // snippet-end:[cloudwatch.go.delete_alarm.args]

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    // snippet-start:[cloudwatch.go.delete_alarm.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudwatch.go.delete_alarm.session]

    err := DeleteAlarm(sess, alarmName)
    if err != nil {
        fmt.Println("Could not delete alarm " + *alarmName)
    } else {
        fmt.Println("Deleted alarm " + *alarmName)
    }
}
// snippet-end:[cloudwatch.go.delete_alarm]
