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
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"strings"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"
	"github.com/aws/aws-sdk-go/service/transcribeservice"
)

type TranscriptResults struct {
	JobName   string `json:"jobName"`
	AccountID string `json:"accountId"`
	Results   struct {
		Transcripts []struct {
			Transcript string `json:"transcript"`
		} `json:"transcripts"`
		Items []struct {
			StartTime    string `json:"start_time,omitempty"`
			EndTime      string `json:"end_time,omitempty"`
			Alternatives []struct {
				Confidence string `json:"confidence"`
				Content    string `json:"content"`
			} `json:"alternatives"`
			Type string `json:"type"`
		} `json:"items"`
	} `json:"results"`
	Status string `json:"status"`
}

func DebugPrint(debug bool, s string) {
	if debug {
		fmt.Println("INFO: " + s)
	}
}

// dropFile adds MP3/4 file to a bucket
// and returns the URI (for StartTranscriptionJob)
func dropFile(debug bool, sess *session.Session, filename string, inputBucket string) (string, error) {
	DebugPrint(debug, "Opening "+filename)
	file, err := os.Open(filename)
	if err != nil {
		fmt.Println("Could not open " + filename)
		return "", err
	}

	defer file.Close()

	uploader := s3manager.NewUploader(sess)

	// Upload the file's body to S3 bucket as an object with the key being the
	// same as the filename.
	DebugPrint(debug, "Uploading "+filename+" to bucket "+inputBucket)
	_, err = uploader.Upload(&s3manager.UploadInput{
		Bucket: aws.String(inputBucket),
		Key:    aws.String(filename),
		Body:   file,
	})
	if err != nil {
		fmt.Println("Failed to upload " + filename + " to bucket " + inputBucket)
		return "", err
	}

	// Create URI based on bucket name and filename
	uri := "s3://" + inputBucket + "/" + filename

	return uri, nil
}

func startTranscription(sess *session.Session, mediaURI string, outputBucket string, jobName string) error {
	svc := transcribeservice.New(sess)

	input := &transcribeservice.StartTranscriptionJobInput{
		LanguageCode: aws.String("en-US"),
		Media: &transcribeservice.Media{
			MediaFileUri: aws.String(mediaURI),
		},
		MediaFormat:          aws.String("wav"),
		OutputBucketName:     aws.String(outputBucket),
		TranscriptionJobName: aws.String(jobName),
	}

	_, err := svc.StartTranscriptionJob(input)
	if err != nil {
		fmt.Println("Got an error starting transcription service:")
		fmt.Println(err)
		return err
	}

	return nil
}

func isTranscriptionDone(sess *session.Session, jobName string) (bool, error) {
	svc := transcribeservice.New(sess)

	input := &transcribeservice.ListTranscriptionJobsInput{
		JobNameContains: aws.String(jobName),
	}

	result, err := svc.ListTranscriptionJobs(input)
	if err != nil {
		return false, err
	}

	if result.TranscriptionJobSummaries[0].TranscriptionJobStatus == nil {
		return false, nil
	}

	// QUEUED | IN_PROGRESS | FAILED | COMPLETED
	if *result.TranscriptionJobSummaries[0].TranscriptionJobStatus == "COMPLETED" {
		return true, nil
	}

	if *result.TranscriptionJobSummaries[0].TranscriptionJobStatus == "FAILED" {
		return false, errors.New("Job failed")
	}

	return false, nil
}

// getFile gets a file from a bucket
func getResultURI(sess *session.Session, jobName string) (string, error) {
	svc := transcribeservice.New(sess)

	input := &transcribeservice.GetTranscriptionJobInput{
		TranscriptionJobName: aws.String(jobName),
	}

	results, err := svc.GetTranscriptionJob(input)
	if err != nil {
		return "", err
	}

	return *results.TranscriptionJob.Transcript.TranscriptFileUri, nil
}

func getTextFromURI(sess *session.Session, outputBucket string, uri string) (string, error) {
	svc := s3.New(sess)
	// The URI should look something like:
	// "s3://" + outputBucket + "/" + filename
	parts := strings.Split(uri, "/")
	filename := parts[len(parts)-1]

	// Now get the guts of the file and return it
	input := &s3.GetObjectInput{
		Bucket: aws.String(outputBucket),
		Key:    aws.String(filename),
	}

	result, err := svc.GetObject(input)
	if err != nil {
		return "", err
	}

	// Body is of type io.ReadCloser
	buf := new(bytes.Buffer)
	buf.ReadFrom(result.Body)
	text := buf.String()

	job := TranscriptResults{}

	// Marshall JSON string in text into job struct
	json.Unmarshal([]byte(text), &job)

	return job.Results.Transcripts[0].Transcript, nil
}

func getTextFromFile(filename string) (string, error) {
	file, err := os.Open(filename)
	if err != nil {
		return "", err
	}
	defer file.Close()

	b, err := ioutil.ReadAll(file)

	return string(b), nil
}

func main() {}
