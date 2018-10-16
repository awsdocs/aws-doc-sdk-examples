/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/cloudtrail"
    "github.com/aws/aws-sdk-go/service/sts"

    "encoding/json"
    "flag"
    "fmt"
    "os"
)

func main() {
    // Get required trail name and bucket name
    var trailName string
    flag.StringVar(&trailName, "n", "", "The name of the trail")
    var bucketName string
    flag.StringVar(&bucketName, "b", "", "the name of bucket to which the trails are uploaded")

    // Option to add CloudTrail policy to bucket
    var addPolicy bool
    flag.BoolVar(&addPolicy, "p", false, "Whether to add the CloudTrail policy to the bucket")

    flag.Parse()

    if trailName == "" || bucketName == "" {
        fmt.Println("You must supply a trail name and bucket name.")
        os.Exit(0)
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    if addPolicy {
        svc := sts.New(sess)
        input := &sts.GetCallerIdentityInput{}

        result, err := svc.GetCallerIdentity(input)
        if err != nil {
            fmt.Println("Got error snarfing caller identity:")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        accountId := aws.StringValue(result.Account)

        s3Policy := map[string]interface{}{
            "Version": "2012-10-17",
            "Statement": []map[string]interface{}{
                {
                    "Sid": "AWSCloudTrailAclCheck20150319",
                    "Effect": "Allow",
                    "Principal": map[string]interface{}{
                        "Service": "cloudtrail.amazonaws.com",
                    },
                    "Action": "s3:GetBucketAcl",
                    "Resource": "arn:aws:s3:::" + bucketName,
                },
                {
                    "Sid": "AWSCloudTrailWrite20150319",
                    "Effect": "Allow",
                    "Principal": map[string]interface{}{
                        "Service": "cloudtrail.amazonaws.com",
                    },
                    "Action": "s3:PutObject",
                    "Resource": "arn:aws:s3:::" + bucketName + "/AWSLogs/" + accountId + "/*",
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
            os.Exit(0)
        }

        // Create S3 service
        s3_svc := s3.New(sess)

        // Now set the policy on the bucket
        _, err = s3_svc.PutBucketPolicy(&s3.PutBucketPolicyInput{
            Bucket: aws.String(bucketName),
            Policy: aws.String(string(policy)),
        })
        if err != nil {
            fmt.Print("Got error adding bucket policy:")
            fmt.Print(err.Error())
            os.Exit(1)
        }

        fmt.Printf("Successfully set bucket %q's policy\n", bucketName)
    }

    // Create CloudTrail client
    svc := cloudtrail.New(sess)

    input := &cloudtrail.CreateTrailInput{
        Name: aws.String(trailName),
        S3BucketName: aws.String(bucketName),
    }

    _, err = svc.CreateTrail(input)
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Created the trail", trailName, "for bucket", bucketName, "in region", regionName)
}
// Tags for sample catalog:

//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Creates an AWS CloudTrail trail.]
//snippet-keyword:[AWS CloudTrail]
//snippet-keyword:[CreateTrail function]
//snippet-keyword:[Go]
//snippet-service:[cloudtrail]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
