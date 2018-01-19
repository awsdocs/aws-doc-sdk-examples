/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"

    "fmt"
    "os"
)

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Polly client
    svc := polly.New(sess)

    resp, err := svc.ListLexicons(nil)
    if err != nil {
        fmt.Println("Got error calling ListLexicons:")
        fmt.Print(err.Error())
        os.Exit(1)
    }

    for _, l := range resp.Lexicons {
        fmt.Println(*l.Name)
        fmt.Println("  Alphabet: " + *l.Attributes.Alphabet)
        fmt.Println("  Language: " + *l.Attributes.LanguageCode)
        fmt.Println("")
    }
}
