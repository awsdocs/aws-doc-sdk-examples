// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[CognitoListUserPools lists your Amazon Cognito user pools.]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[ListUserPools function]
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
// snippet-start:[cognito.go.list_user_pools]
package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cognitoidentityprovider"

    "fmt"
    "os"
)

func main() {
     // Initialize a session that the SDK will use to load configuration,
    // credentials, and region from the shared config file. (~/.aws/config).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Cognito client
    svc := cognitoidentityprovider.New(sess)

    max := int64(10)

    result, err := svc.ListUserPools(
        &cognitoidentityprovider.ListUserPoolsInput{
            MaxResults: &max,
        }) // .ListBuckets(nil)
    if err != nil {
        fmt.Println("Could not list user pools")
        os.Exit(1)
    }

    fmt.Println("User pools:")
    fmt.Println("")

    for _, pool := range result.UserPools {
        fmt.Println("Name: " + aws.StringValue(pool.Name))
        fmt.Println("ID:   " + aws.StringValue(pool.Id))
        fmt.Println("Created: " + aws.TimeValue(pool.CreationDate).String())
        fmt.Println("")
    }
}
// snippet-end:[cognito.go.list_user_pools]
