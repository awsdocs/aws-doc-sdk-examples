//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Deletes an EC2 security group.]
//snippet-keyword:[Amazon Elastic Compute Cloud]
//snippet-keyword:[DeleteSecurityGroup function]
//snippet-keyword:[Go]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-03-16]
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
    "fmt"
    "os"
    "path/filepath"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/awserr"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)

// Deletes a security group by the ID passed in.
//
// Usage:
//    go run ec2_delete_security_group.go group_id
func main() {
    if len(os.Args) != 2 {
        exitErrorf("Security Group ID required\nUsage: %s group_id",
            filepath.Base(os.Args[0]))
    }
    groupID := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create an EC2 service client.
    svc := ec2.New(sess)

    // Delete the security group.
    _, err := svc.DeleteSecurityGroup(&ec2.DeleteSecurityGroupInput{
        GroupId: aws.String(groupID),
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
        exitErrorf("Unable to get descriptions for security groups, %v.", err)
    }

    fmt.Printf("Successfully delete security group %q.\n", groupID)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
