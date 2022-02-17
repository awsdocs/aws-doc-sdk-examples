// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.list_subnet_groups]
package main

// snippet-start:[rds.go.list_subnet_groups.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
)
// snippet-end:[rds.go.list_subnet_groups.imports]

// GetSubnetGroups retrieves your Amazon RDS subnet groups
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of the subnet groups and nil
//     Otherwise, nil and an error from the call to DescribeDBSubnetGroups
func GetSubnetGroups(sess *session.Session) (*rds.DescribeDBSubnetGroupsOutput, error) {
    // snippet-start:[rds.go.list_subnet_groups.call]
    svc := rds.New(sess)

    result, err := svc.DescribeDBSubnetGroups(nil)
    // snippet-end:[rds.go.list_subnet_groups.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[rds.go.list_subnet_groups.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[rds.go.list_subnet_groups.session]

    result, err := GetSubnetGroups(sess)
    if err != nil {
        fmt.Println("Got an error retrieving subnet groups:")
        fmt.Println(err)
        return
    }

    if len(result.DBSubnetGroups) < 1 {
        fmt.Println("Could not find any subnet groups")
        return
    }

    for _, s := range result.DBSubnetGroups {
        fmt.Println("* " + *s.DBSubnetGroupName + " in VpcId: " + *s.VpcId)
    }
}
// snippet-end:[rds.go.list_subnet_groups]
