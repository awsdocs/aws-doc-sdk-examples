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

package main

import (
    "encoding/json"
    "fmt"
    "os"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/sts"
)

// ConfigureBucket configures a bucket to send event activity to CloudTrail
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     trailName is the name of the trail
//     bucketName is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to sts.GetCallerIdentity, json.Marshal, or s3.PutBucketPolicy
// snippet-start:[cloudtrail.go.configure_trail]
func ConfigureBucket(sess *session.Session, bucketName string) error {
    stsSvc := sts.New(sess)
    // snippet-start:[cloudtrail.go.configure_trail.policy]
    input := &sts.GetCallerIdentityInput{}

    result, err := stsSvc.GetCallerIdentity(input)
    if err != nil {
        return err
    }

    accountID := aws.StringValue(result.Account)

    // Create a policy so the bucket sends events to CloudTrail
    s3Policy := map[string]interface{}{
        "Version": "2012-10-17",
        "Statement": []map[string]interface{}{
            {
                "Sid":    "AWSCloudTrailAclCheck20150319",
                "Effect": "Allow",
                "Principal": map[string]interface{}{
                    "Service": "cloudtrail.amazonaws.com",
                },
                "Action":   "s3:GetBucketAcl",
                "Resource": "arn:aws:s3:::" + bucketName,
            },
            {
                "Sid":    "AWSCloudTrailWrite20150319",
                "Effect": "Allow",
                "Principal": map[string]interface{}{
                    "Service": "cloudtrail.amazonaws.com",
                },
                "Action":   "s3:PutObject",
                "Resource": "arn:aws:s3:::" + bucketName + "/AWSLogs/" + accountID + "/*",
                "Condition": map[string]interface{}{
                    "StringEquals": map[string]interface{}{
                        "s3:x-amz-acl": "bucket-owner-full-control",
                    },
                },
            },
        },
    }

    policy, err := json.Marshal(s3Policy)
    if err != nil {
        return err
    }

    // Create an S3 service
    s3Svc := s3.New(sess)

    // Set the policy on the bucket
    _, err = s3Svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
        Bucket: aws.String(bucketName),
        Policy: aws.String(string(policy)),
    })
    if err != nil {
        return err
    }
    // snippet-end:[cloudtrail.go.configure_trail.policy]

    return nil
}
// snippet-end:[cloudtrail.go.configure_trail]

// CreateTrail creates a trail to get event activity from a bucket
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     trailName is the name of the trail
//     bucketName is the name of the bucket
// Output:
//     If success, nil
//     Otherwise, an error from the call to sts.GetCallerIdentity, json.Marshal, or s3.PutBucketPolicy
// snippet-start:[cloudtrail.go.create_trail]
func CreateTrail(sess *session.Session, trailName string, bucketName string) error {
    // snippet-start:[cloudtrail.go.create_trail.create]
    svc := cloudtrail.New(sess)

    ctInput := &cloudtrail.CreateTrailInput{
        Name:         aws.String(trailName),
        S3BucketName: aws.String(bucketName),
    }

    _, err := svc.CreateTrail(ctInput)
    if err != nil {
        return err
    }

    return nil
    // snippet-end:[cloudtrail.go.create_trail.create]
}
// snippet-end:[cloudtrail.go.create_trail]

// GetTrails gets a list of trails
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of Trail objects and nil
//     Otherwise, a nil object and an error from a call to DescribeTrails
func GetTrails(sess *session.Session) ([]*cloudtrail.Trail, error) {
    var trails []*cloudtrail.Trail
    svc := cloudtrail.New(sess)

    resp, err := svc.DescribeTrails(nil)
    if err != nil {
        return trails, err
    }

    return resp.TrailList, nil
}

// listTrails displays a list of your trails
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, nil
//     Otherwise, an error returned by a call to DescribeTrails
// snippet-start:[cloudtrail.go.list_trails]
func listTrails(sess *session.Session) error {
    svc := cloudtrail.New(sess)

    resp, err := svc.DescribeTrails(nil)
    if err != nil {
        return err
    }

    fmt.Println("Found", len(resp.TrailList), "trail(s):")
    fmt.Println("")

    for _, trail := range resp.TrailList {
        fmt.Println("Trail name:  " + *trail.Name)
        fmt.Println("Bucket name: " + *trail.S3BucketName)
        fmt.Println("")
    }

    return nil
}
// snippet-end:[cloudtrail.go.list_trails]

// DeleteTrail deletes a trail
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     trailName is the name of the trail
// Output:
//     If success, nil
//     Otherwise, an error from a call to DeleteTrail
// snippet-start:[cloudtrail.go.delete_trail]
func DeleteTrail(sess *session.Session, trailName string) error {
    svc := cloudtrail.New(sess)

    input := &cloudtrail.DeleteTrailInput{
        Name: aws.String(trailName),
    }

    _, err := svc.DeleteTrail(input)

    return err
}
// snippet-end:[cloudtrail.go.delete_trail]

// GetUser retrieves the name of the logged on user
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the user name and nil
//     Otherwise, an empty string and an error from a call to GetCallerIdentity
func GetUser(sess *session.Session) (string, error) {
    svc := sts.New(sess)
    id, err := svc.GetCallerIdentity(nil)
    if err != nil {
        return "", err
    }

    // arn:aws:iam::ACCOUNT-ID:user/USER
    parts := strings.Split(*id.Arn, "/")
    userName := parts[1]

    return userName, nil
}

// GetTrailEvents gets the events for a trail
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     trailName is the name of the trail
// Output:
//     If success, a list of Event objects and nil
//     Otherwise, a nil object and an error from a call to LookupEvents
func GetTrailEvents(sess *session.Session, trailName string) ([]*cloudtrail.Event, error) {
    var events []*cloudtrail.Event
    svc := cloudtrail.New(sess)
    input := &cloudtrail.LookupEventsInput{EndTime: aws.Time(time.Now())}

    resp, err := svc.LookupEvents(input)
    if err != nil {
        return events, err
    }

    return resp.Events, nil
}

// listTrailEvents lists the events for the user in a trail
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     trailName is the name of the trail
//     userName is the name of the user
// Output:
//     If success, nil
//     Otherwise, an error from a call to LookupEvents
// snippet-start:[cloudtrail.go.list_trail_events]
func listTrailEvents(sess *session.Session, trailName string, userName string) error {
    svc := cloudtrail.New(sess)
    input := &cloudtrail.LookupEventsInput{EndTime: aws.Time(time.Now())}

    resp, err := svc.LookupEvents(input)
    if err != nil {
        return err
    }

    fmt.Println("Events for user " + userName + " in trail " + trailName + ":")

    for _, event := range resp.Events {
        if userName == *event.Username {
            fmt.Println("")
            fmt.Println("Event:")
            fmt.Println("")
            fmt.Println("Name    ", aws.StringValue(event.EventName))
            fmt.Println("ID:     ", aws.StringValue(event.EventId))
            fmt.Println("Time:   ", aws.TimeValue(event.EventTime))

            fmt.Println("Resourcs:")

            for _, resource := range event.Resources {
                fmt.Println("  Name:", aws.StringValue(resource.ResourceName))
                fmt.Println("  Type:", aws.StringValue(resource.ResourceType))
            }
        }
    }

    return nil
}
// snippet-end:[cloudtrail.go.list_trail_events]

// usage displays information on using this app
// Inputs:
//     none
// Output:
//     none
func usage() {
    fmt.Println("Usage:")
    fmt.Println("")
    fmt.Println("    go run sqsQueueOps.go OPERATION")
    fmt.Println("")
    fmt.Println("where OPERATION is one of:")
    fmt.Println("    -l")
    fmt.Println("        list your trails")
    fmt.Println("    -c  TRAIL-NAME BUCKET-NAME")
    fmt.Println("        create trail TRAIL-NAME that gets events from bucket BUCKET-NAME")
    fmt.Println("    -d  TRAIL-NAME")
    fmt.Println("        delete trail TRAIL-NAME")
    fmt.Println("    -e  USER-NAME TRAIL-NAME")
    fmt.Println("        show events from user USER-NAME for trail TRAIL-NAME")
    fmt.Println("    -h")
    fmt.Println("        print this message and quit")
    fmt.Println("")
}

func main() {
    // Parse args ourselves so user can only request one operation
    op := ""
    numOps := 0 // So we know when user has requested more than one
    length := len(os.Args)
    i := 1
    trailName := ""
    bucketName := ""
    userName := ""

    for i < length {
        switch os.Args[i] {
        case "-h":
            usage()
            return
        case "-c":
            // -c TRAIL-NAME BUCKET-NAME
            if length != 3 {
                fmt.Println("You must supply a trail name and bucket name")
                usage()
                return
            }
            op = "create"
            i++
            trailName = os.Args[i]
            i++
            bucketName = os.Args[i]
            numOps++

        case "-d":
            // -d TRAIL-NAME
            op = "delete"
            i++
            trailName = os.Args[i]
            numOps++

        case "-e":
            // -e TRAIL-NAME
            op = "events"
            i++
            userName = os.Args[i]
            i++
            trailName = os.Args[i]
            numOps++

        case "-l":
            // -l
            op = "list"
            numOps++

        default:
            fmt.Println("Unrecognized option: " + os.Args[i])
            usage()
            return
        }

        i++
    }

    if numOps > 1 {
        fmt.Println("You cannot request more than one operation")
        usage()
        return
    }

    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    switch op {
    case "create":
        if trailName == "" || bucketName == "" {
            fmt.Println("You must supply a trail name and a bucket name")
            usage()
            return
        }

        err := CreateTrail(sess, trailName, bucketName)
        if err != nil {
            fmt.Println("Got an error creating trail:")
            fmt.Println(err)
            return
        }

    case "delete":
        if trailName == "" {
            fmt.Println("You must supply a trail name")
            usage()
            return
        }

        err := DeleteTrail(sess, trailName)
        if err != nil {
            fmt.Println("Got an error deleting trail:")
            fmt.Println(err)
            return
        }

    case "events":
        if userName == "" || trailName == "" {
            fmt.Println("You must supply a user name and trail name")
            usage()
            return
        }

        err := listTrailEvents(sess, trailName, userName)
        if err != nil {
            fmt.Println("Got an error listing trail events:")
            fmt.Println(err)
            return
        }

    case "list":
        err := listTrails(sess)
        if err != nil {
            fmt.Println("Got an error listing trails:")
            fmt.Println(err)
            return
        }

    default:
        // This should never happen
        fmt.Println("Unrecognized operation (typo?): " + op)
        return
    }
}
