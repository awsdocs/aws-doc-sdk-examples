package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/kms"
	"github.com/aws/aws-sdk-go-v2/service/kms/types"
)

type KMSCreateKeyImpl struct{}

func (dt KMSCreateKeyImpl) CreateKey(ctx context.Context,
	params *kms.CreateKeyInput,
	optFns ...func(*kms.Options)) (*kms.CreateKeyOutput, error) {

	if len(params.Tags) == 0 {
		return nil, errors.New("You must supply at least one tag value")
	}

	keyMetadata := &types.KeyMetadata{KeyId: aws.String("aws-docs-example-kmskeyID")}

	output := &kms.CreateKeyOutput{
		KeyMetadata: keyMetadata,
	}

	return output, nil
}

type Config struct {
	Key   string `json:"Key"`
	Value string `json:"Value"`
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

	if globalConfig.Key == "" || globalConfig.Value == "" {
		msg := "You must supply a value for Key and Value in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestCreateKey(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &KMSCreateKeyImpl{}

	input := &kms.CreateKeyInput{
		Tags: []types.Tag{
			{
				TagKey:   &globalConfig.Key,
				TagValue: &globalConfig.Value,
			},
		},
	}

	result, err := MakeKey(context.Background(), api, input)
	if err != nil {
		fmt.Println("Got error creating key:")
		fmt.Println(err)
		return
	}

	t.Log("KeyID: " + *result.KeyMetadata.KeyId)
}
