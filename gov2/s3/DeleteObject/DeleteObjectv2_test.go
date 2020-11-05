package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type S3DeleteObjectImpl struct{}

func (dt S3DeleteObjectImpl) DeleteObject(ctx context.Context,
	params *s3.DeleteObjectInput,
	optFns ...func(*s3.Options)) (*s3.DeleteObjectOutput, error) {

	output := &s3.DeleteObjectOutput{}

	return output, nil
}

type Config struct {
	BucketName string `json:"BucketName"`
	ObjectName string `json:"ObjectName"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration(t *testing.T) error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.BucketName == "" || globalConfig.ObjectName == "" {
		msg := "You must specify a value for BucketName and ObjectName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDeleteObject(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.DeleteObjectInput{
		Bucket: &globalConfig.BucketName,
		Key:    &globalConfig.ObjectName,
	}

	api := &S3DeleteObjectImpl{}

	_, err = DeleteItem(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Deleted " + globalConfig.ObjectName + " from bucket " + globalConfig.BucketName)
}
