 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Go]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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

sess, err := session.NewSession()
if err != nil {
    fmt.Println("failed to create session,", err)
    return
}

svc := cloudwatch.New(sess)

params := &cloudwatch.DeleteAlarmsInput{
    AlarmNames: []*string{ // Required
        aws.String("AlarmName"), // Required
        // More values...
    },
}
resp, err := svc.DeleteAlarms(params)

if err != nil {
    // Print the error, cast err to awserr.Error to get the Code and
    // Message from an error.
    fmt.Println(err.Error())
    return
}

// Pretty-print the response data.
fmt.Println(resp)
