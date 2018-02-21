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
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3crypto"

    "fmt"
    "os"
    "bytes"
)

func main() {
    if len(os.Args) != 2 {
       fmt.Println("You must supply a key")
       os.Exit(1)
    }

    key := os.Args[1]
    bucket := "myBucket"
    object := "myObject"

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file ~/.aws/credentials
    // and configuration from the shared configuration file ~/.aws/config.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create a decryption client
    // We need to pass the session here so S3 can use it. In addition, any decryption that
    // occurs will use the KMS client.
    svc := s3crypto.NewDecryptionClient(sess)

    input := &s3.GetObjectInput{
        Bucket: aws.String(bucket),
        Key:    aws.String(object),
    }

    resp, err := svc.GetObject(input)
    if err != nil {
        fmt.Println("Got an error getting object from bucket")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    // Save the object
    outFile, err := os.Create(obj)
    if err != nil {
        fmt.Println("Got error creating file " + obj + ":")
        fmt.Println(err.Error())
        os.Exit(1)
    }

    defer outFile.Close()

    _, err = io.Copy(outFile, resp.Body)
    if err != nil {
        fmt.Println("Got error saving response:")
        fmt.Println(err.Error())
    }

    fmt.Println("Saved " + obj + " from bucket " + bucket + ":")

    /* To print it out as a string:
    buf := new(bytes.Buffer)
    buf.ReadFrom(resp.Body)
    newStr := buf.String()

    fmt.Printf(newStr)
    */
}
