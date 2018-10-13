//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
    //go get -u github.com/aws/aws-sdk-go
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/ses"
    "github.com/aws/aws-sdk-go/aws/awserr"

    "fmt"
)

func main() {
    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create an SES session.
    svc := ses.New(sess)
    
    // Attempt to send the email.
    result, err := svc.GetSendStatistics(nil)
    
    // Display any error message
    if err != nil {
        fmt.Println(err.Error())
        return
    }

    dps := result.SendDataPoints

    fmt.Println("Got", len(dps), "datapoints")
    fmt.Println("")

    for _, dp := range dps {
        fmt.Println("Timestamp: ", dp.Timestamp)
        fmt.Println("Attempts:  ", aws.Int64Value(dp.DeliveryAttempts))
        fmt.Println("Bounces:   ", aws.Int64Value(dp.Bounces))
        fmt.Println("Complaints:", aws.Int64Value(dp.Complaints))
        fmt.Println("Rejects:   ", aws.Int64Value(dp.Rejects))
        fmt.Println("")
    }
}
