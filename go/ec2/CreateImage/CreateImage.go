// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.create_image]
package main

// snippet-start:[ec2.go.create_image.imports]
import (
    "flag"
    "fmt"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
    "github.com/aws/aws-sdk-go/service/ec2/ec2iface"
)
// snippet-end:[ec2.go.create_image.imports]

// MakeImage creates an Amazon Elastic Compute Cloud (Amazon EC2) image.
// Inputs:
//     svc is an Amazon EC2 service client
//     description is the description of the image
//     instanceID is the ID of the instance
//     name is the name of the image
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func MakeImage(svc ec2iface.EC2API, description, instanceID, name *string) (*ec2.CreateImageOutput, error) {
    // snippet-start:[ec2.go.create_image.call]
    opts := &ec2.CreateImageInput{
        Description: description,
        InstanceId:  instanceID,
        Name:        name,
        BlockDeviceMappings: []*ec2.BlockDeviceMapping{
            {
                DeviceName: aws.String("/dev/sda1"),
                NoDevice:   aws.String(""),
            },
            {
                DeviceName: aws.String("/dev/sdb"),
                NoDevice:   aws.String(""),
            },
            {
                DeviceName: aws.String("/dev/sdc"),
                NoDevice:   aws.String(""),
            },
        },
    }
    resp, err := svc.CreateImage(opts)
    // snippet-end:[ec2.go.create_image.call]
    if err != nil {
        return nil, err
    }

    return resp, nil
}

func main() {
    // snippet-start:[ec2.go.create_image.args]
    description := flag.String("d", "", "The description of the image")
    instanceID := flag.String("i", "", "The ID of the instance")
    name := flag.String("n", "", "The name of the image")
    flag.Parse()

    if *description == "" || *instanceID == "" || *name == "" {
        fmt.Println("You must supply an image description, instance ID, and image name")
        fmt.Println("(-d IMAGE-DESCRIPTION -i INSTANCE-ID -n IMAGE-NAME")
        return
    }
    // snippet-end:[ec2.go.create_image.args]

    // snippet-start:[ec2.go.create_image.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := ec2.New(sess)
    // snippet-end:[ec2.go.create_image.session]

    resp, err := MakeImage(svc, description, instanceID, name)
    if err != nil {
        fmt.Println("Got an error creating image:")
        fmt.Println(err)
        return
    }

    fmt.Println("ID: ", resp.ImageId)
}
// snippet-end:[ec2.go.create_image]
