// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"
	"github.com/google/uuid"
)

type Config struct {
	Bucket string `json:"Bucket"`
}

var configFileName = "config.json"
var globalConfig Config

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	err = json.Unmarshal(content, &globalConfig)
	if err != nil {
		return err
	}

	t.Log("Bucket: " + globalConfig.Bucket)
	return nil
}

func createBucket(sess *session.Session, bucket *string) error {
	svc := s3.New(sess)

	_, err := svc.CreateBucket(&s3.CreateBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

func uploadFile(sess *session.Session, bucket, key *string) error {
	file, err := os.Create(*key)
	if err != nil {
		return err
	}
	defer file.Close()

	uploader := s3manager.NewUploader(sess)
	_, err = uploader.Upload(&s3manager.UploadInput{
		Bucket: bucket,
		Key:    key,
		Body:   file,
	})
	if err != nil {
		return err
	}

	return nil
}

func deleteBucket(sess *session.Session, bucket *string) error {
	svc := s3.New(sess)

	iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
		Bucket: bucket,
	})

	err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter)
	if err != nil {
		return err
	}

	_, err = svc.DeleteBucket(&s3.DeleteBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
		Bucket: bucket,
	})
	if err != nil {
		return err
	}

	return nil
}

func TestListFolders(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	bucketCreated := false

	if globalConfig.Bucket == "" {
		id := uuid.New()
		globalConfig.Bucket = "test-bucket-" + id.String()

		err := createBucket(sess, &globalConfig.Bucket)
		if err != nil {
			t.Fatal(err)
		}

		bucketCreated = true
		t.Log("Created bucket " + globalConfig.Bucket)
	}

	// Upload a test file to simulate folders
	testKey := "folder1/test.txt"
	err = uploadFile(sess, &globalConfig.Bucket, &testKey)
	if err != nil {
		t.Fatal(err)
	}

	// Call ListFolders and validate output
	folders, err := ListFolders(sess, &globalConfig.Bucket)
	if err != nil {
		t.Fatal(err)
	}

	if len(folders) == 0 {
		t.Error("expected some folders, got none")
	}

	for _, folder := range folders {
		t.Log("Folder:", folder)
	}

	// Clean up
	if bucketCreated {
		err := deleteBucket(sess, &globalConfig.Bucket)
		if err != nil {
			t.Log("You'll have to delete " + globalConfig.Bucket + " manually")
			t.Fatal(err)
		}

		t.Log("Deleted bucket " + globalConfig.Bucket)
	}
}
