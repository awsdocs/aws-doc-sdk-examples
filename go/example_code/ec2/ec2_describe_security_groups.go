// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Describes your EC2 security groups.]
// snippet-keyword:[Amazon Elastic Compute Cloud]
// snippet-keyword:[DescribeSecurityGroups function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[ec2]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "fmt"
    "os"
    "path/filepath"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)

// Describes the security groups by IDs that are passed into the CLI. Takes
// a space separated list of group IDs as input.
//
// Usage:
//    go run ec2_describe_security_groups.go groupId1 groupId2 ...
func main() {
    if len(os.Args) < 2 {
        exitErrorf("Security Group ID required\nUsage: %s group_id ...",
            filepath.Base(os.Args[0]))
    }
    groupIds := os.Args[1:]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create an EC2 service client.
    svc := ec2.New(sess)

    // Retrieve the security group descriptions
    result, err := svc.DescribeSecurityGroups(&ec2.DescribeSecurityGroupsInput{
        GroupIds: aws.StringSlice(groupIds),
    })
    if err != nil {
        if aerr, ok := err.(awserr.Error); ok {
            switch aerr.Code() {
            case "InvalidGroupId.Malformed":
                fallthrough
            case "InvalidGroup.NotFound":
                exitErrorf("%s.", aerr.Message())
            }
        }
        exitErrorf("Unable to get descriptions for security groups, %v", err)
    }

    fmt.Println("Security Group:")
    for _, group := range result.SecurityGroups {
        fmt.Println(group)
    }
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
