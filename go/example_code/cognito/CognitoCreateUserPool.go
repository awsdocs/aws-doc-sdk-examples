// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[CognitoCreateUserPool creates an Amazon Cognito user pool.]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[CreateUserPool function]
// snippet-keyword:[Go]
// snippet-service:[cognito]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-12]
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
// snippet-start:[cognito.go.create_user_pool]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cognitoidentityprovider"
    "github.com/aws/aws-sdk-go/service/iam"

    "fmt"
    "os"
    "strings"
)

func getRoleName(poolName string) string {
    name := strings.Replace(poolName, "-", "", -1)
    return name + "-SMS-Role"
}

// Creates Cognito user pool POOL_NAME
//
// Usage:
//    go run CognitoCreateUserPool.go POOL_NAME
func main() {
    if len(os.Args) < 2 {
        fmt.Println("Pool name is required")
        fmt.Println("Usage: go run", os.Args[0], "POOL_NAME")
    }

    poolName := os.Args[2]

    emailMsg := "{username} {####}" // Must match regex: [\p{L}\p{M}\p{S}\p{N}\p{P}\s*]*\{####\}[\p{L}\p{M}\p{S}\p{N}\p{P}\s*]*
    emailSubject := "AWS TW Chat"
    smsMsg := "{username} {####}" // Must match regex: .*\{####\}.*

    waitDays := int64(1)

    emailVerifyMsg := "{####}" // Must match regex: [\p{L}\p{M}\p{S}\p{N}\p{P}\s*]*\{####\}[\p{L}\p{M}\p{S}\p{N}\p{P}\s*]*
    emailVerifySub := "AWS TW Chat"
    smsAuthMsg := "{####}"   // Must match regex: .*\{####\}.*
    smsVerifyMsg := "{####}" // Must match regex: .*\{####\}.*

    // Initialize a session that the SDK will use to load configuration,
    // credentials, and region from the shared config file. (~/.aws/config).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    //     // Create SMS role so pool can msg new users on your behalf
    iamSvc := iam.New(sess)

    doc := "{ \"Version\": \"2012-10-17\", \"Statement\": [ { \"Sid\": \"\", \"Effect\": \"Allow\", \"Principal\": { \"Service\": \"cognito-idp.amazonaws.com\" }, \"Action\": \"sts:AssumeRole\" } ] }"

    // Create SMS role with pool name, less any hyphens
    roleName := getRoleName(poolName) // Required

    path := "/service-role/"

    iamResp, iamErr := iamSvc.CreateRole(
        &iam.CreateRoleInput{
            AssumeRolePolicyDocument: &doc,
            RoleName:                 &roleName,
            Path:                     &path})

    if iamErr != nil {
        fmt.Println("Could not create role")
        os.Exit(1)
    }

    roleArn := iamResp.Role.Arn
    roleID := iamResp.Role.RoleId

    // Create Cognito client
    cgSvc := cognitoidentityprovider.New(sess)

    params := &cognitoidentityprovider.CreateUserPoolInput{
        PoolName: &poolName, // Required
        AdminCreateUserConfig: &cognitoidentityprovider.AdminCreateUserConfigType{
            AllowAdminCreateUserOnly: aws.Bool(false), // false == users can sign themselves up
            InviteMessageTemplate: &cognitoidentityprovider.MessageTemplateType{
                EmailMessage: &emailMsg,     // Welcome message to new users
                EmailSubject: &emailSubject, // Welcome subject to new users
                SMSMessage:   &smsMsg,
            },
            UnusedAccountValidityDays: &waitDays, // How many days to wait before rescinding offer
        },
        AutoVerifiedAttributes: []*string{ // Auto-verified means the user confirmed the SNS message
            aws.String("email"), // Required; either email or phone_number
            aws.String("phone_number"),
        },
        EmailVerificationMessage: &emailVerifyMsg,
        EmailVerificationSubject: &emailVerifySub,
        Policies: &cognitoidentityprovider.UserPoolPolicyType{
            PasswordPolicy: &cognitoidentityprovider.PasswordPolicyType{
                MinimumLength:    aws.Int64(6), // Require a password of at least 6 chars
                RequireLowercase: aws.Bool(false),
                RequireNumbers:   aws.Bool(false),
                RequireSymbols:   aws.Bool(false),
                RequireUppercase: aws.Bool(false),
            },
        },
        Schema: []*cognitoidentityprovider.SchemaAttributeType{
            { // Required
                AttributeDataType:      aws.String("String"),
                DeveloperOnlyAttribute: aws.Bool(false),
                Mutable:                aws.Bool(false),
                Name:                   aws.String("user_name"),
                Required:               aws.Bool(false),
                StringAttributeConstraints: &cognitoidentityprovider.StringAttributeConstraintsType{
                    MaxLength: aws.String("64"), // user name can be up to 64 chars
                    MinLength: aws.String("3"),  // or as few as 3 chars
                },
            },
        },
        SmsAuthenticationMessage: &smsAuthMsg,
        SmsConfiguration: &cognitoidentityprovider.SmsConfigurationType{
            SnsCallerArn: roleArn, // Required
            ExternalId:   roleID,
        },
        SmsVerificationMessage: &smsVerifyMsg,
    }

    fmt.Println("")

    cgResp, cgErr := cgSvc.CreateUserPool(params)

    if cgErr != nil {
        fmt.Println("Could not create user pool")
        os.Exit(1)
    }

    fmt.Println("")
    fmt.Println(cgResp)
}
// snippet-end:[cognito.go.create_user_pool]
