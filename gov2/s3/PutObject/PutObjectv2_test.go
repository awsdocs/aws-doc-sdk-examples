package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type S3PutObjectImpl struct{}

func (dt S3PutObjectImpl) PutObject(ctx context.Context,
	params *s3.PutObjectInput,
	optFns ...func(*s3.Options)) (*s3.PutObjectOutput, error) {

	output := &s3.PutObjectOutput{
		VersionId: aws.String("1.0"),
	}

	return output, nil
}

type Config struct {
	BucketName string `json:"BucketName"`
	FileName   string `json:"FileName"`
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

	if globalConfig.BucketName == "" || globalConfig.FileName == "" {
		msg := "You must specify a value for BucketName and FileName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPubObject(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.PutObjectInput{
		Bucket: &globalConfig.BucketName,
		Key:    &globalConfig.FileName,
	}

	api := &S3PutObjectImpl{}

	resp, err := PutFile(context.TODO(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Uploaded version " + *resp.VersionId + " of " + globalConfig.FileName + " to bucket " + globalConfig.BucketName)
}
