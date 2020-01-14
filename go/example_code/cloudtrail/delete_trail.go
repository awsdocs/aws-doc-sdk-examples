// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Deletes an AWS CloudTrail trail.]
// snippet-keyword:[AWS CloudTrail]
// snippet-keyword:[DeleteTrail function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[cloudtrail]
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
// snippet-start:[cloudtrail.go.delete_trail.complete]
package main

// snippet-start:[cloudtrail.go.delete_trail.imports]
import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/cloudtrail"

    "flag"
    "fmt"
    "os"
)
// snippet-end:[cloudtrail.go.delete_trail.imports]

func main() {
    // Trail name required
    // snippet-start:[cloudtrail.go.delete_trail.vars]
    trailNamePtr := flag.String("n", "", "The name of the trail to delete")

    flag.Parse()

    if *trailNamePtr == "" {
        fmt.Println("You must supply a trail name")
        os.Exit(1)
    }
    // snippet-end:[cloudtrail.go.delete_trail.vars]

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    // snippet-start:[cloudtrail.go.delete_trail.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))
    // snippet-end:[cloudtrail.go.delete_trail.session]

    // snippet-start:[cloudtrail.go.delete_trail.delete]
    svc := cloudtrail.New(sess)

    _, err := svc.DeleteTrail(&cloudtrail.DeleteTrailInput{Name: aws.String(*trailNamePtr)})
    if err != nil {
        fmt.Println("Got error calling CreateTrail:")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    fmt.Println("Successfully deleted trail", *trailNamePtr)
    // snippet-end:[cloudtrail.go.delete_trail.delete]
}
// snippet-end:[cloudtrail.go.delete_trail.complete]
