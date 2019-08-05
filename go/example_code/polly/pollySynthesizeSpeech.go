// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Gets text from a file and produces an MP3 file containing the synthesized speech.]
// snippet-keyword:[Amazon Polly]
// snippet-keyword:[SynthesizeSpeech function]
// snippet-keyword:[Go]
// snippet-service:[polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
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

package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"

    "fmt"
    "os"
    "strings"
    "io"
    "io/ioutil"
)

func main() {
    if len(os.Args) != 2 {
        fmt.Println("You must supply an alarm name")
        os.Exit(1)
    }

    // The name of the text file to convert to MP3
    fileName := os.Args[1]

    // Open text file and get it's contents as a string
    contents, err := ioutil.ReadFile(fileName)
    if err != nil {
        fmt.Println("Got error opening file " + fileName)
        fmt.Print(err.Error())
        os.Exit(1)
    }

    // Convert bytes to string
    s := string(contents[:])

    // Initialize a session that the SDK uses to load
    // credentials from the shared credentials file. (~/.aws/credentials).
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create Polly client
    svc := polly.New(sess)

    // Output to MP3 using voice Joanna
    input := &polly.SynthesizeSpeechInput{OutputFormat: aws.String("mp3"), Text: aws.String(s), VoiceId: aws.String("Joanna")}

    output, err := svc.SynthesizeSpeech(input)
    if err != nil {
        fmt.Println("Got error calling SynthesizeSpeech:")
        fmt.Print(err.Error())
        os.Exit(1)
    }

    // Save as MP3
    names := strings.Split(fileName, ".")
    name := names[0]
    mp3File := name + ".mp3"

    outFile, err := os.Create(mp3File)
    if err != nil {
        fmt.Println("Got error creating " + mp3File + ":")
        fmt.Print(err.Error())
        os.Exit(1)
    }

    defer outFile.Close()
    _, err = io.Copy(outFile, output.AudioStream)
    if err != nil {
        fmt.Println("Got error saving MP3:")
        fmt.Print(err.Error())
        os.Exit(1)
    }
}
