package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/kinesis"
)

type KinesisPutRecordImpl struct{}

func (pr KinesisPutRecordImpl) PutRecord(ctx context.Context,
	params *kinesis.PutRecordInput,
	optFns ...func(*kinesis.Options)) (*kinesis.PutRecordOutput, error) {

	output := &kinesis.PutRecordOutput{
		ShardId: aws.String("shard-01"),
	}

	return output, nil
}

type Config struct {
	StreamName   string `json:"StreamName"`
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

	if globalConfig.StreamName == "" {
		msg := "You must specify a value for StreamName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPutRecord(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	input := kinesis.PutRecordInput{
		StreamName:   &globalConfig.StreamName,
	}

	api := &KinesisPutRecordImpl{}

	resp, err := MakePutRecord(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Put record on shard " + *resp.ShardId)
}
