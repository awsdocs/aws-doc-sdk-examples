//snippet-sourceauthor: [Doug-AWS]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

2018
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
    "github.com/aws/aws-sdk-go/service/cloudtrail"

    "flag"
    "fmt"
    "os"
)

func main() {
    // Trail name required
    var trailName string
    flag.StringVar(&trailName, "n", "", "The name of the trail to delete")

    flag.Parse()

    if trailName == "" {
        fmt.Println("You must supply a trail name")
        os.Exit(1)
    }

    // Initialize a session in us-west-2 that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess, err := session.NewSession(&aws.Config{
        Region: aws.String("us-west-2")},
    )

    // Create CloudTrail client
    svc := cloudtrail.New(sess)

    _, err = svc.DeleteTrail(&cloudtrail.DeleteTrailInput{Name: trailNamePtr})
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Successfully deleted trail", trailName)
}
