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
// snippet-sourcedate:[2020-1-6]
/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
// snippet-start:[cognito.go.create_user.complete]
package main

// snippet-start:[cognito.go.create_user.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

    "flag"
    "fmt"
    "os"
)
// snippet-end:[cognito.go.create_user.imports]

func main() {
    // snippet-start:[cognito.go.create_user.vars]
    emailIDPtr := flag.String("e", "", "The email address of the user")
    userPoolIDPtr := flag.String("p", "", "The ID of the user pool")
    userNamePtr := flag.String("n", "", "The name of the user")

    flag.Parse()

    if *emailIDPtr == "" || *userPoolIDPtr == "" || *userNamePtr == "" {
        fmt.Println("You must supply an email address, user pool ID, and user name")
        fmt.Println("Usage: go run CreateUser.go -e EMAIL-ADDRESS -p USER-POOL-ID -n USER-NAME")
        os.Exit(1)
    }
    // snippet-end:[cognito.go.create_user.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[cognito.go.create_user.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cognito.go.create_user.session]

    // snippet-start:[cognito.go.create_user.create]
    cognitoClient := cognitoidentityprovider.New(sess)

    newUserData := &cognitoidentityprovider.AdminCreateUserInput{
        DesiredDeliveryMediums: []*string{
            aws.String("EMAIL"),
        },
        UserAttributes: []*cognitoidentityprovider.AttributeType{
            {
                Name:  aws.String("email"),
                Value: aws.String(*emailIDPtr),
            },
        },
    }

    newUserData.SetUserPoolId(*userPoolIDPtr)
    newUserData.SetUsername(*userNamePtr)

    _, err := cognitoClient.AdminCreateUser(newUserData)
    if err != nil {
        fmt.Println("Got error creating user:", err)
    }
    // snippet-end:[cognito.go.create_user.create]
}
// snippet-end:[cognito.go.create_user.complete]
