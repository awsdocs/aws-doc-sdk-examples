// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[polly.go.synthesize_speech]
package main

// snippet-start:[polly.go.synthesize_speech.imports]
import (
    "errors"
    "flag"
    "fmt"
    "io"
    "io/ioutil"
    "os"
    "strings"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/polly"
    "github.com/aws/aws-sdk-go/service/polly/pollyiface"
)
// snippet-end:[polly.go.synthesize_speech.imports]

// MakeSpeech synthesizes the text in a file to produce an MP3 file.
// Inputs:
//     svc is an Amazon Polly service client
//     fileName is the name of the file containing text to synthesize
// Output:
//     If success, information about the speech and nil
//     Otherwise, nil and an error from the call to ReadFile or SynthesizeSpeech
func MakeSpeech(svc pollyiface.PollyAPI, fileName *string) (*polly.SynthesizeSpeechOutput, error) {
    // snippet-start:[polly.go.synthesize_speech.read_file]
    contents, err := ioutil.ReadFile(*fileName)
    // snippet-end:[polly.go.synthesize_speech.read_file]
    if err != nil {
        return nil, errors.New("Got error opening file " + *fileName)
    }

    // snippet-start:[polly.go.synthesize_speech.call]
    s := string(contents[:])

    output, err := svc.SynthesizeSpeech(&polly.SynthesizeSpeechInput{
        OutputFormat: aws.String("mp3"),
        Text:         aws.String(s),
        VoiceId:      aws.String("Joanna"),
    })
    // snippet-end:[polly.go.synthesize_speech.call]
    return output, err
}

func main() {
    // snippet-start:[polly.go.synthesize_speech.args]
    fileName := flag.String("f", "", "The file to tranlate into speech")
    flag.Parse()

    if *fileName == "" {
        fmt.Println("You must supply a file name (-f FILENAME)")
    }
    // snippet-end:[polly.go.synthesize_speech.args]

    // snippet-start:[polly.go.synthesize_speech.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := polly.New(sess)
    // snippet-end:[polly.go.synthesize_speech.session]

    output, err := MakeSpeech(svc, fileName)
    if err != nil {
        fmt.Println("Got error calling SynthesizeSpeech:")
        fmt.Print(err)
        return
    }

    // snippet-start:[polly.go.synthesize_speech.save_file]
    // Save as MP3
    names := strings.Split(*fileName, ".")
    name := names[0]
    mp3File := name + ".mp3"

    outFile, err := os.Create(mp3File)
    if err != nil {
        fmt.Println("Got error creating " + mp3File + ":")
        fmt.Print(err)
        return
    }

    defer outFile.Close()
    _, err = io.Copy(outFile, output.AudioStream)
    if err != nil {
        fmt.Println("Got error saving MP3:")
        fmt.Print(err)
        return
    }
    // snippet-end:[polly.go.synthesize_speech.save_file]
}
// snippet-end:[polly.go.synthesize_speech]
