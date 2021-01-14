package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/iam"
)

type IAMDeleteAccountAliasImpl struct{}

func (dt IAMDeleteAccountAliasImpl) DeleteAccountAlias(ctx context.Context,
	params *iam.DeleteAccountAliasInput,
	optFns ...func(*iam.Options)) (*iam.DeleteAccountAliasOutput, error) {

	output := &iam.DeleteAccountAliasOutput{}

	return output, nil
}

type Config struct {
	Alias string `json:"Alias"`
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

	if globalConfig.Alias == "" {
		msg := "You must supply a value for Alias in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestDeleteAccountAlias(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMDeleteAccountAliasImpl{}

	input := &iam.DeleteAccountAliasInput{
		AccountAlias: &globalConfig.Alias,
	}

	_, err = RemoveAccountAlias(context.TODO(), api, input)
	if err != nil {
		t.Log("Got an error deleting an account alias")
		t.Log(err)
		return
	}

	t.Log("Deleted account alias " + globalConfig.Alias)
}
