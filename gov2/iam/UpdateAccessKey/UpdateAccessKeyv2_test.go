package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

type IAMUpdateAccessKeyImpl struct{}

func (dt IAMUpdateAccessKeyImpl) UpdateAccessKey(ctx context.Context,
	params *iam.UpdateAccessKeyInput,
	optFns ...func(*iam.Options)) (*iam.UpdateAccessKeyOutput, error) {

	if nil == params.AccessKeyId || *params.AccessKeyId == "" || nil == params.UserName || *params.UserName == "" {
		msg := "AccessKeyId or UserName is nil or an empty string"
		return nil, errors.New(msg)
	}

	output := &iam.UpdateAccessKeyOutput{}

	return output, nil
}

type Config struct {
	KeyID    string `json:"KeyID"`
	UserName string `json:"UserName"`
}

var configFileName = "config.json"

var globalConfig Config

func populateConfiguration() error {
	content, err := ioutil.ReadFile(configFileName)
	if err != nil {
		return err
	}

	text := string(content)

	err = json.Unmarshal([]byte(text), &globalConfig)
	if err != nil {
		return err
	}

	if globalConfig.KeyID == "" || globalConfig.UserName == "" {
		msg := "You must supply a value for KeyID and UserName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestUpdateAccessKey(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMUpdateAccessKeyImpl{}

	input := &iam.UpdateAccessKeyInput{
		AccessKeyId: &globalConfig.KeyID,
		Status:      types.StatusTypeActive,
		UserName:    &globalConfig.UserName,
	}

	_, err = ActivateKey(context.TODO(), api, input)
	if err != nil {
		t.Log("Error", err)
		return
	}

	t.Log("Access Key activated")
}
