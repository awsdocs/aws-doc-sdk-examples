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
    "strings"
    "testing"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)

// snippet-end:[sqs.go.imports]

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

func addItemsToBucket(sess *session.Session, bucketName string) error {
    svc := s3.New(sess)

    _, err := svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: aws.String(bucketName),
        Key:    aws.String("dummy1"),
    })
    if err != nil {
        return err
    }

    _, err = svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: aws.String(bucketName),
        Key:    aws.String("dummy2"),
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

func TestCloudTrailOps(t *testing.T) {
    err := PopulateConfiguration()
    if err != nil {
        t.Fatal(err)
    }

    // snippet-start:[sqs.go.session]
    // Create a session using credentials from ~/.aws/credentials
    // and the region from ~/.aws/config
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[sqs.go.session]

    // Create a random, unique value
    id := uuid.New()

    // Use it to create a trail name and bucket name
    trailName := "MyTrail-" + id.String()
    // Bucket names must be all lower-case, no underscores, dashes and numbers ok
    bucketName := "mybucket-" + id.String()

    err = createBucket(sess, bucketName)
    if err != nil {
        t.Fatal(err)
    }

    // Create a trail
    err = CreateTrail(sess, trailName, bucketName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Created trail " + trailName)

    err = addItemsToBucket(sess, bucketName)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Added items to bucket")

    err = showTrails(sess, t)
    if err != nil {
        t.Fatal(err)
    }

    // Get user
    user, err := GetUser(sess)
    if err != nil {
        t.Fatal(err)
    }

    // List any events initiated by user
    err = showEvents(t, sess, trailName, user)
    if err != nil {
        t.Fatal(err)
    }

    // Now delete the trail
    err = DeleteTrail(sess, trailName)
    if err != nil {
        t.Log("You'll have to delete this trail yourself:  " + trailName)
        t.Log("You'll have to delete this bucket yourself: " + bucketName)
        t.Fatal(err)
    }

    t.Log("Deleted trail " + trailName)

    // Delete the bucket
    err = deleteBucket(sess, bucketName)
    if err != nil {
        t.Log("You'll have to delete this bucket yourself: " + bucketName)
        t.Fatal(err)
    }
}
