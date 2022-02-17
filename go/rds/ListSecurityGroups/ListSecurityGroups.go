// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.list_security_groups]
package main

// snippet-start:[rds.go.list_security_groups.imports]
import (
    "fmt"

    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
)
// snippet-end:[rds.go.list_security_groups.imports]

// GetSecurityGroups retrieves your Amazon RDS security groups
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
// Output:
//     If success, a list of security groups and nil
//     Otherwise, nil and an error from the call to DescribeDBSecurityGroups
func GetSecurityGroups(sess *session.Session) (*rds.DescribeDBSecurityGroupsOutput, error) {
    svc := rds.New(sess)

    result, err := svc.DescribeDBSecurityGroups(nil)
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[rds.go.list_security_groups.session]
    // (import "github.com/aws/aws-sdk-go/aws/session")
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[rds.go.list_security_groups.session]

    result, err := GetSecurityGroups(sess)
    if err != nil {
        fmt.Println("Got an error retrieving security groups:")
        fmt.Println(err)
        return
    }

    if len(result.DBSecurityGroups) < 1 {
        fmt.Println("Could not find any security groups")
        return
    }

    for _, s := range result.DBSecurityGroups {
        fmt.Print("* " + *s.DBSecurityGroupName)

        if s.VpcId != nil {
            fmt.Println("  in VpcId: " + *s.VpcId)
        } else {
            fmt.Println("")
        }
    }
}
// snippet-end:[rds.go.list_security_groups]
