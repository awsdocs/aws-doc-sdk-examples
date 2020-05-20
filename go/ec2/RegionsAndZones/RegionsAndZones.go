// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[ec2.go.regions_and_zones]
package main

// snippet-start:[ec2.go.regions_and_zones.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ec2"
)
// snippet-end:[ec2.go.regions_and_zones.imports]

// GetRegions retrieves the AWS Regions where Amazon Elastic Compute Cloud (Amazon EC2) is available.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of AWS Regions and nil
//     Otherwise, nil and an error from the call to DescribeRegions
func GetRegions(sess *session.Session) (*ec2.DescribeRegionsOutput, error) {
    // snippet-start:[ec2.go.regions_and_zones.regions]
    svc := ec2.New(sess)

    resultRegions, err := svc.DescribeRegions(nil)
    // snippet-end:[ec2.go.regions_and_zones.regions]
    if err != nil {
        return nil, err
    }

    return resultRegions, nil
}

// GetZones retrieves the availability zones within the current region.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, the list of availability zones and nil
//     Otherwise, nil and an error from the call to
func GetZones(sess *session.Session) (*ec2.DescribeAvailabilityZonesOutput, error) {
    svc := ec2.New(sess)

    // snippet-start:[ec2.go.regions_and_zones.zones]
    resultAvalZones, err := svc.DescribeAvailabilityZones(nil)
    // snippet-end:[ec2.go.regions_and_zones.zones]
    if err != nil {
        return nil, err
    }

    return resultAvalZones, nil
}

func main() {
    // snippet-start:[ec2.go.regions_and_zones.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[ec2.go.regions_and_zones.session]

    resultRegions, err := GetRegions(sess)
    if err != nil {
        fmt.Println("Got an error retrieving the regions:")
        fmt.Println(err)
        return
    }

    resultAvalZones, err := GetZones(sess)
    if err != nil {
        fmt.Println("Got an error retrieving the availability zones:")
        fmt.Println(err)
        return
    }

    // snippet-start:[ec2.go.regions_and_zones.display]
    fmt.Println("Regions:", resultRegions.Regions)
    fmt.Println("")
    fmt.Println("Zones:  ", resultAvalZones.AvailabilityZones)
    fmt.Println("")
    fmt.Println("Found", len(resultRegions.Regions), "regions; found", len(resultAvalZones.AvailabilityZones), "availability zones", "in", *sess.Config.Region)
    // snippet-end:[ec2.go.regions_and_zones.display]
}
// snippet-end:[ec2.go.regions_and_zones]
