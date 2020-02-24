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

// snippet-start:[sqs.go.imports]
import (
    "encoding/json"
    "io/ioutil"
    "strings"
    "testing"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)

// snippet-end:[sqs.go.imports]

// Config defines a set of configuration values
type Config struct {
    TrailName  string `json:"TrailName"`
    BucketName string `json:"BucketName"`
}

// ConfigFile defines the name of the file containing configuration values
var ConfigFile = "config.json"

// GlobalConfig contains the configuration values
var GlobalConfig Config

// PopulateConfiguration gets values from config.json and populates the configuration struct GlobalConfig
// Inputs:
//     none
// Output:
//     If success, nil
//     Otherwise, an error from reading or parsing the configuration file
func PopulateConfiguration() error {
    // Get configuration from config.json

    // Get entire file as a JSON string
    content, err := ioutil.ReadFile(ConfigFile)
    if err != nil {
        return err
    }

    // Convert []byte to string
    text := string(content)

    // Marshall JSON string in text into global struct
    err = json.Unmarshal([]byte(text), &GlobalConfig)
    if err != nil {
        return err
    }

    return nil
}

func createBucket(sess *session.Session, bucketName string) error {
    // Create the bucket
    svc := s3.New(sess)
    input := &s3.CreateBucketInput{
        Bucket: aws.String(bucketName),
    }
    _, err := svc.CreateBucket(input)
    if err != nil {
        return err
    }

    // Wait until bucket exists
    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: aws.String(bucketName),
    })
    if err != nil {
        return err
    }

    return nil
}

func addItemToBucket(sess *session.Session, bucketName string, objectName string) error {
    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: aws.String(bucketName),
        Key:    aws.String(objectName),
    })
    if err != nil {
        return err
    }

    return nil
}

func deleteItemFromBucket(sess *session.Session, bucketName string, objectName string) error {
    svc := s3.New(sess)

    _, err := svc.DeleteObject(&s3.DeleteObjectInput{
        Bucket: aws.String(bucketName),
        Key:    aws.String(objectName),
    })
    if err != nil {
        return err
    }

    return nil
}

func deleteBucket(sess *session.Session, bucketName string) error {
    svc := s3.New(sess)

    // First delete any items in the bucket
    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: aws.String(bucketName),
    })

    err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
    if err != nil {
        return err
    }

    _, err = svc.DeleteBucket(&s3.DeleteBucketInput{
        Bucket: aws.String(bucketName),
    })
    if err != nil {
        return err
    }

    return nil
}

func showEvents(t *testing.T, sess *session.Session, trailName string, userName string) error {
    t.Log("Getting events for user " + userName)

    events, err := GetTrailEvents(sess, trailName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Events for " + trailName + ":")

    for _, event := range events {
        if nil == event.Username {
            continue
        }

        if userName == *event.Username {
            t.Log("")
            t.Log("Event:")
            if nil != event.EventName {
                t.Log("Name    ", aws.StringValue(event.EventName))
            }
            if nil != event.EventId {
                t.Log("ID:     ", aws.StringValue(event.EventId))
            }
            if nil != event.EventTime {
                t.Log("Time:   ", aws.TimeValue(event.EventTime))
            }

            t.Log("Resourcs:")

            for _, resource := range event.Resources {
                if nil != resource.ResourceName {
                    t.Log("  Name:", aws.StringValue(resource.ResourceName))
                }
                if nil != resource.ResourceType {
                    t.Log("  Type:", aws.StringValue(resource.ResourceType))
                }
            }
        }
    }

    return nil
}

func showTrails(sess *session.Session, t *testing.T) error {
    trails, err := GetTrails(sess)
    if err != nil {
        return err
    }

    for _, trail := range trails {
        t.Log("Trail name:  " + *trail.Name)
        t.Log("Bucket name: " + *trail.S3BucketName)
        t.Log("")
    }

    return nil
}

func logErrors(t *testing.T, dummy1 string, dummy2 string, bucketCreated bool, bucket string, trailCreated bool, trail string) {
    if dummy1 != "" {
        t.Log("You'll have to delete this bucket item yourself: " + dummy1)
    }
    if dummy2 != "" {
        t.Log("You'll have to delete this bucket item yourself: " + dummy2)
    }
    if bucketCreated {
        t.Log("You'll have to delete this bucket yourself: " + bucket)
    }
    if trailCreated {
        t.Log("You'll have to delete this trail yourself: " + trail)
    }
}

func TestCloudTrailOps(t *testing.T) {
    err := PopulateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    bucketName := GlobalConfig.BucketName
    bucketCreated := false
    trailName := GlobalConfig.TrailName
    trailCreated := false
    dummy1 := ""
    dummy2 := ""

    // snippet-start:[sqs.go.session]
    // Create a session using credentials from ~/.aws/credentials
    // and the region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.session]

    // Create a random, unique value
    id := uuid.New()

    if bucketName == "" {
        // Bucket names must be all lower-case, no underscores, dashes and numbers ok
        bucketName = "mybucket-" + id.String()

        t.Log("Creating bucket " + bucketName)
        err = createBucket(sess, bucketName)
        if err != nil {
            t.Fatal(err)
        }

        bucketCreated = true
        t.Log("Created bucket " + bucketName)
    } else {
        t.Log("Using existing bucket " + bucketName)
    }

    err = ConfigureBucket(sess, bucketName)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    if trailName == "" {
        trailName = "MyTrail-" + id.String()
        // Create a trail
        t.Log("Creating trail " + trailName)
        // CreateTrail(sess *session.Session, trailName string, bucketName string) error
        err = CreateTrail(sess, trailName, bucketName)
        if err != nil {
            logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
            t.Fatal(err)
        }

        trailCreated = true
        t.Log("Created trail " + trailName)
    } else {
        t.Log("Using existing trail " + trailName)
    }

    // Add a couple of dummy items to the bucket
    dummy1 = "dummy1-" + id.String()

    err = addItemToBucket(sess, bucketName, dummy1)
    if err != nil {
        dummy1 = ""
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    t.Log("Added " + dummy1 + " to bucket " + bucketName)

    dummy2 = "dummy2-" + id.String()
    err = addItemToBucket(sess, bucketName, dummy2)
    if err != nil {
        dummy2 = ""
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    t.Log("Added " + dummy2 + " to bucket " + bucketName)

    err = showTrails(sess, t)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    // Get user
    user, err := GetUser(sess)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    // List any events initiated by user
    err = showEvents(t, sess, trailName, user)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    // Now delete the trail, if created
    if trailCreated {
        err = DeleteTrail(sess, trailName)
        if err != nil {
            logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
            t.Fatal(err)
        }

        t.Log("Deleted trail " + trailName)
        trailCreated = false
    }

    // Delete dummy objects in bucket
    err = deleteItemFromBucket(sess, bucketName, dummy1)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    t.Log("Deleted " + dummy1 + " from bucket " + bucketName)
    dummy1 = ""

    err = deleteItemFromBucket(sess, bucketName, dummy2)
    if err != nil {
        logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
        t.Fatal(err)
    }

    t.Log("Deleted " + dummy2 + " from bucket " + bucketName)
    dummy2 = ""

    // Delete the bucket, if created
    if bucketCreated {
        err = deleteBucket(sess, bucketName)
        if err != nil {
            logErrors(t, dummy1, dummy2, bucketCreated, bucketName, trailCreated, trailName)
            t.Fatal(err)
        }
    }
}
