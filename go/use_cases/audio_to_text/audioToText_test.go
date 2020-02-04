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
package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"strconv"
	"testing"
	"time"

	guuid "github.com/google/uuid"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
)

type Config struct {
	Region       string `json:"Region"`
	InputBucket  string `json:"InputBucket"`
	OutputBucket string `json:"OutputBucket"`
	AudioFile    string `json:"AudioFile"`
	ResultFile   string `json:"ResultFile"`
	SleepSeconds int    `json:"SleepSeconds"`
	JobName      string `json:"JobName"`
	Debug        bool   `json:"Debug"`
}

func multiplyDuration(factor int64, d time.Duration) time.Duration {
	return time.Duration(factor) * d
}

func getNameOrDefault(defaultName string, envName string) string {
	name := os.Getenv(envName)

	if name == "" {
		return defaultName
	}

	return name
}

/*
func DebugPrint(debug bool, s string) {
	if debug {
		fmt.Println("INFO: " + s)
	}
}
*/

func TestAudioToText(t *testing.T) {
	// When the test started:
	thisTime := time.Now()
	nowString := thisTime.Format("20060102150405")
	fmt.Println("Starting unit test at " + nowString)
	fmt.Println("")

	// Get configuration from config.json
	configFileName := "config.json"

	// Get entire file as a JSON string
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		log.Fatal(err)
	}

	// Convert []byte to string
	text := string(content)
	config := Config{}

	// Marshall JSON string in text into job struct
	json.Unmarshal([]byte(text), &config)

	// Add timestamp to job name
	config.JobName = config.JobName + "-" + nowString

	DebugPrint(config.Debug, "Got configuration values:")
	DebugPrint(config.Debug, "Region:       "+config.Region)
	DebugPrint(config.Debug, "InputBucket:  "+config.InputBucket)
	DebugPrint(config.Debug, "OutputBucket: "+config.OutputBucket)
	DebugPrint(config.Debug, "AudioFile:    "+config.AudioFile)
	DebugPrint(config.Debug, "ResultFile:   "+config.ResultFile)
	seconds := strconv.Itoa(config.SleepSeconds)
	DebugPrint(config.Debug, "SleepSeconds: "+seconds)
	DebugPrint(config.Debug, "JobName:      "+config.JobName)

	// Create random string
	id := guuid.New()
	defaultName := id.String()

	// Get region from environment variable AWS_REGION
	region := getNameOrDefault(config.Region, "AWS_REGION")

	/*
		Bucket names can be up to 63 characters long,
		and can contain only lower-case characters, numbers, periods, and dashes.
	*/

	// Get input bucket name from INPUT_BUCKET env variable
	inputBucket := config.InputBucket

	if inputBucket == "" {
		inputBucket = "input-" + defaultName
	}

	inputBucket = getNameOrDefault(inputBucket, "INPUT_BUCKET")

	// Get output bucket name from OUTPUT_BUCKET env variable
	outputBucket := config.OutputBucket

	if outputBucket == "" {
		outputBucket = "output-" + defaultName
	}
	outputBucket = getNameOrDefault(outputBucket, "OUTPUT_BUCKET")

	// Get audio file name from AUDIO_FILE env variable
	audioFile := getNameOrDefault(config.AudioFile, "AUDIO_FILE")

	// Get results file name from RESULTS_FILE env variable
	resultsFile := getNameOrDefault(config.ResultFile, "RESULTS_FILE")

	// Get how long to wait, in seconds, for file to show up in output bucket from SLEEP_SECONDS env variable
	sleepSecondsStr := getNameOrDefault(seconds, "SLEEP_SECONDS")

	sleepSeconds, err := strconv.ParseInt(sleepSecondsStr, 10, 64)
	if err != nil {
		fmt.Println("Got an error parsing " + sleepSecondsStr + " as an integer")
		fmt.Println(err)
		os.Exit(1)
	}

	// Create a session, in the specified region, to use for all operations,
	// using default credentials
	sess := session.Must(session.NewSession(&aws.Config{
		Region: aws.String(region),
	}))

	// Create input and output buckets
	err = createBucket(sess, inputBucket)
	if err != nil {
		fmt.Println("Could not create bucket " + inputBucket)
		os.Exit(1)
	} else {
		fmt.Println("Created bucket " + inputBucket)
	}

	err = createBucket(sess, outputBucket)
	if err != nil {
		fmt.Println("Could not create bucket " + outputBucket)
		os.Exit(1)
	} else {
		fmt.Println("Created bucket " + outputBucket)
	}

	fmt.Println("")

	// Upload audio file to bucket
	fileURI, err := dropFile(config.Debug, sess, audioFile, inputBucket)
	if err != nil {
		fmt.Println("Failed to drop " + audioFile + " into bucket " + inputBucket)
		fmt.Println(err)
		os.Exit(1)
	} else {
		DebugPrint(config.Debug, "Dropped "+audioFile+" into bucket "+inputBucket)
	}

	err = startTranscription(sess, fileURI, outputBucket, config.JobName)
	if err != nil {
		fmt.Println("Failed to start transcribing " + audioFile)
		fmt.Println(err)
		os.Exit(1)
	} else {
		fmt.Println("Started transcription")
	}

	// loop until the job is done
	// Wait for sleepSeconds seconds (is there a better way???)
	ts := multiplyDuration(sleepSeconds, time.Second)

	for true {
		done, err := isTranscriptionDone(sess, config.JobName)
		if err != nil {
			fmt.Println("Transcribing " + audioFile + " failed")
			fmt.Println(err)
			os.Exit(1)
		}

		if done {
			fmt.Println("")
			fmt.Println("Transcription complete")
			break
		} else {
			fmt.Print(".")
		}

		time.Sleep(ts)
	}

	fmt.Println("")

	// Now get results of transcription
	resultsURI, err := getResultURI(sess, config.JobName)
	if err != nil {
		fmt.Println("Failed to get text from " + outputBucket)
		fmt.Println(err)
		os.Exit(1)
	}

	text, err = getTextFromURI(sess, outputBucket, resultsURI)
	if err != nil {
		fmt.Println("Could not get results from bucket " + outputBucket)
		fmt.Println(err)
		os.Exit(1)
	}

	expected, err := getTextFromFile(resultsFile)
	if err != nil {
		fmt.Println("Could not get text from " + resultsFile)
		fmt.Println(err)
		os.Exit(1)
	}

	// Compare the results with the expected results
	if text == expected {
		fmt.Println("Got the expected results: ")
		fmt.Println("    " + text)
	} else {
		fmt.Println("Did NOT get the expected results. Got:")
		fmt.Println("'" + text + "'")
		fmt.Println("instead of:")
		fmt.Println("'" + expected + "'")

	}

	fmt.Println("")

	// Clean up
	err = deleteBucket(sess, inputBucket)
	if err != nil {
		fmt.Println("Could not delete bucket: " + inputBucket)
		os.Exit(1)
	} else {
		fmt.Println("Deleted bucket " + inputBucket)
	}

	err = deleteBucket(sess, outputBucket)
	if err != nil {
		fmt.Println("Could not delete bucket: " + outputBucket)
		os.Exit(1)
	} else {
		fmt.Println("Deleted bucket " + outputBucket)
	}

	fmt.Println("")
}
