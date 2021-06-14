// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[create_trail.go demonstrates how to create a CloudTrail trail.]
// snippet-keyword:[Amazon CloudTrail]
// snippet-keyword:[CreateTrail function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cloudtrail]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[cloudtrail.go.create_trail.complete]
package main

// snippet-start:[cloudtrail.go.create_trail.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/sts"

    "encoding/json"
    "flag"
    "fmt"
)

// snippet-end:[cloudtrail.go.create_trail.imports]

func main() {
    // Get required trail name and bucket name
    // snippet-start:[cloudtrail.go.create_trail.vars]
    trailNamePtr := flag.String("n", "", "The name of the trail")
    bucketNamePtr := flag.String("b", "", "the name of the bucket to which the trails are uploaded")
    addPolicyPtr := flag.Bool("p", false, "Whether to add the CloudTrail policy to the bucket")

    flag.Parse()

    if *trailNamePtr == "" || *bucketNamePtr == "" {
        fmt.Println("You must supply a trail name and bucket name.")
        return
    }
    // snippet-end:[cloudtrail.go.create_trail.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[cloudtrail.go.create_trail.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudtrail.go.create_trail.session]

    // snippet-start:[cloudtrail.go.create_trail.policy]
    if *addPolicyPtr {
        svc := sts.New(sess)
        input := &sts.GetCallerIdentityInput{}

        result, err := svc.GetCallerIdentity(input)
        if err != nil {
            fmt.Println("Got error snarfing caller identity:")
            fmt.Println(err.Error())
            return
        }

        accountID := aws.StringValue(result.Account)

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
                    "Resource": "arn:aws:s3:::" + *bucketNamePtr,
                },
                {
                    "Sid":    "AWSCloudTrailWrite20150319",
                    "Effect": "Allow",
                    "Principal": map[string]interface{}{
                        "Service": "cloudtrail.amazonaws.com",
                    },
                    "Action":   "s3:PutObject",
                    "Resource": "arn:aws:s3:::" + *bucketNamePtr + "/AWSLogs/" + accountID + "/*",
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
            fmt.Println("Error marshalling request")
            return
        }

        // Create S3 service
        s3Svc := s3.New(sess)

        // Now set the policy on the bucket
        _, err = s3Svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
            Bucket: aws.String(*bucketNamePtr),
            Policy: aws.String(string(policy)),
        })
        if err != nil {
            fmt.Print("Got error adding bucket policy:")
            fmt.Print(err.Error())
            return
        }

        fmt.Printf("Successfully set bucket %q's policy\n", *bucketNamePtr)
    }
    // snippet-end:[cloudtrail.go.create_trail.policy]

    // snippet-start:[cloudtrail.go.create_trail.create]
    svc := cloudtrail.New(sess)

    input := &cloudtrail.CreateTrailInput{
        Name:         aws.String(*trailNamePtr),
        S3BucketName: aws.String(*bucketNamePtr),
    }

    _, err := svc.CreateTrail(input)
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        return
    }

    fmt.Println("Created the trail", *trailNamePtr, "for bucket", *bucketNamePtr)
    // snippet-end:[cloudtrail.go.create_trail.create]
}

// snippet-end:[cloudtrail.go.create_trail.complete]
