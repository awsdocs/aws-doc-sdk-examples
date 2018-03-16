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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)

// Attempts to allocate an VPC Elastic IP Address for region. The IP
// address will be associated with the instance ID passed in.
//
// Usage:
//    go run ec2_allocate_address.go INSTANCE_ID
func main() {
    if len(os.Args) != 2 {
        exitErrorf("instance ID required\nUsage: %s instance_id",
            filepath.Base(os.Args[0]))
    }
    instanceID := os.Args[1]

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create an EC2 service client.
    svc := ec2.New(sess)

    // Attempt to allocate the Elastic IP address.
    allocRes, err := svc.AllocateAddress(&ec2.AllocateAddressInput{
        Domain: aws.String("vpc"),
    })
    if err != nil {
        exitErrorf("Unable to allocate IP address, %v", err)
    }

    // Associate the new Elastic IP address with an existing EC2 instance.
    assocRes, err := svc.AssociateAddress(&ec2.AssociateAddressInput{
        AllocationId: allocRes.AllocationId,
        InstanceId:   aws.String(instanceID),
    })
    if err != nil {
        exitErrorf("Unable to associate IP address with %s, %v",
            instanceID, err)
    }

    fmt.Printf("Successfully allocated %s with instance %s.\n\tallocation id: %s, association id: %s\n",
        *allocRes.PublicIp, instanceID, *allocRes.AllocationId, *assocRes.AssociationId)
}

func exitErrorf(msg string, args ...interface{}) {
    fmt.Fprintf(os.Stderr, msg+"\n", args...)
    os.Exit(1)
}
