// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[CognitoCreateUser.go creates a user in a Amazon Cognito user pool.]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[AdminCreateUser function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
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
// snippet-start:[cognito.go.create_user]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

    "flag"
    "fmt"
    "os"
)

func main() {
    emailIDptr := flag.String("e", "", "The email address of the user")
    userPoolIdptr := flag.String("p", "", "The ID of the user pool")
    userNameptr := flag.String("n", "", "The name of the user")

    flag.Parse()

    emailID := *emailIDptr
    userPoolID := *userPoolIdptr
    userName := *userNameptr

    if emailID == "" || userPoolID == "" || userName == "" {
        fmt.Println("You must supply an email address, user pool ID, and user name")
        fmt.Println("Usage: go run CreateUser.go -e EMAIL-ADDRESS -p USER-POOL-ID -n USER-NAME")
        os.Exit(1)
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )
    if err != nil {
        fmt.Println("Got error creating session:", err)
        os.Exit(1)
    }

    // Create Cognito service client
    cognitoClient := cognitoidentityprovider.New(sess)

    newUserData := &cognitoidentityprovider.AdminCreateUserInput{
        DesiredDeliveryMediums: []*string{
            aws.String("EMAIL"),
        },
        UserAttributes: []*cognitoidentityprovider.AttributeType{
            {
                Name:  aws.String("email"),
                Value: aws.String(emailID),
            },
        },
    }

    newUserData.SetUserPoolId(userPoolID)
    newUserData.SetUsername(userName)

    _, err = cognitoClient.AdminCreateUser(newUserData)
    if err != nil {
        fmt.Println("Got error creating user:", err)
    }
}
// snippet-end:[cognito.go.create_user]
