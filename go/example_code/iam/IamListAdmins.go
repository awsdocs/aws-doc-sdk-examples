//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Lists the IAM users that have adminstrator privileges.]
//snippet-keyword:[AWS Identity and Access Management]
//snippet-keyword:[ListGroupPolicies function]
//snippet-keyword:[ListAttachedGroupPolicies function]
//snippet-keyword:[ListGroupsForUser function]
//snippet-keyword:[GetAccountAuthorizationDetails function]
//snippet-keyword:[Go]
//snippet-service:[iam]
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
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"

    "fmt"
    "os"
)

func UserPolicyHasAdmin(user *iam.UserDetail, admin string) bool {
    for _, policy := range user.UserPolicyList {
        if *policy.PolicyName == admin {
            return true
        }
    }

    return false
}

func AttachedUserPolicyHasAdmin(user *iam.UserDetail, admin string) bool {
    for _, policy := range user.AttachedManagedPolicies {
        if *policy.PolicyName == admin {
            return true
        }
    }

    return false
}

func GroupPolicyHasAdmin(svc *iam.IAM, group *iam.Group, admin string) bool {
    input := &iam.ListGroupPoliciesInput{
        GroupName: group.GroupName,
    }

    result, err := svc.ListGroupPolicies(input)
    if err != nil {
        fmt.Println("Got error calling ListGroupPolicies for group", group.GroupName)
    }

    // Wade through policies
    for _, policyName := range result.PolicyNames {
        if
        *policyName == admin {
            return true
        }
    }

    return false
}

func AttachedGroupPolicyHasAdmin(svc *iam.IAM, group *iam.Group, admin string) bool {
    input := &iam.ListAttachedGroupPoliciesInput{GroupName: group.GroupName}
    result, err := svc.ListAttachedGroupPolicies(input)
    if err != nil {
        fmt.Println("Got error getting attached group policies:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    for _, policy := range result.AttachedPolicies {
        if *policy.PolicyName == admin {
            return true
        }
    }

    return false
}

func UsersGroupsHaveAdmin(svc *iam.IAM, user *iam.UserDetail, admin string) bool {
    input := &iam.ListGroupsForUserInput{UserName: user.UserName}
    result, err := svc.ListGroupsForUser(input)
    if err != nil {
        fmt.Println("Got error getting groups for user:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    for _, group := range result.Groups {
        groupPolicyHasAdmin := GroupPolicyHasAdmin(svc, group, admin)

        if groupPolicyHasAdmin {
            return true
        }

        attachedGroupPolicyHasAdmin := AttachedGroupPolicyHasAdmin(svc, group, admin)

        if attachedGroupPolicyHasAdmin {
            return true
        }
    }

    return false
}

func IsUserAdmin(svc *iam.IAM, user *iam.UserDetail, admin string) bool {
    // Check policy, attached policy, and groups (policy and attached policy)
    policyHasAdmin := UserPolicyHasAdmin(user, admin)
    if policyHasAdmin {
        return true
    }

    attachedPolicyHasAdmin := AttachedUserPolicyHasAdmin(user, admin)
    if attachedPolicyHasAdmin {
        return true
    }

    userGroupsHaveAdmin := UsersGroupsHaveAdmin(svc, user, admin)
    if userGroupsHaveAdmin {
        return true
    }

    return false
}

func main() {
    sess, err := session.NewSession()
    if err != nil {
        fmt.Println("Got error creating new session")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    svc := iam.New(sess, &aws.Config{Region: aws.String("us-west-2")})

    numUsers := 0
    numAdmins := 0

    // Get list of users
    user := "User"
    input := &iam.GetAccountAuthorizationDetailsInput{Filter: []*string{&user}}
    resp, err := svc.GetAccountAuthorizationDetails(input)
    if err != nil {
        fmt.Println("Got error getting account details")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // The policy name that indicates administrator access
    adminName := "AdministratorAccess"

    // Wade through resulting users
    for _, user := range resp.UserDetailList {
        numUsers += 1

        isAdmin := IsUserAdmin(svc, user, adminName)

        if isAdmin {
            fmt.Println(*user.UserName)
            numAdmins += 1
        }
    }

    // Are there more?
    for *resp.IsTruncated {
        input := &iam.GetAccountAuthorizationDetailsInput{Filter: []*string{&user}, Marker: resp.Marker}
        resp, err = svc.GetAccountAuthorizationDetails(input)
        if err != nil {
            fmt.Println("Got error getting account details")
            fmt.Println(err.Error())
            os.Exit(1)
        }

        // Wade through resulting users
        for _, user := range resp.UserDetailList {
            numUsers += 1

            isAdmin := IsUserAdmin(svc, user, adminName)

            if isAdmin {
                fmt.Println(*user.UserName)
                numAdmins += 1
            }
        }
    }

    fmt.Println("")
    fmt.Println("Found", numAdmins, "admin(s) out of", numUsers, "user(s).")
}
