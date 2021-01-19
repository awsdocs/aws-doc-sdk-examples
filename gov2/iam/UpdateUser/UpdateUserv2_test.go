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

type IAMUpdateUserImpl struct{}

func (dt IAMUpdateUserImpl) UpdateUser(ctx context.Context,
	params *iam.UpdateUserInput,
	optFns ...func(*iam.Options)) (*iam.UpdateUserOutput, error) {
	return &iam.UpdateUserOutput{}, nil
}

type Config struct {
	UserName string `json:"UserName"`
	NewName  string `json:"NewName"`
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

	if globalConfig.UserName == "" || globalConfig.NewName == "" {
		msg := "You must supply a value for UserName and NewName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestUpdateUser(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMUpdateUserImpl{}

	input := &iam.UpdateUserInput{
		UserName:    &globalConfig.UserName,
		NewUserName: &globalConfig.NewName,
	}

	_, err = RenameUser(context.Background(), api, input)
	if err != nil {
		t.Log("Got an error updating user " + globalConfig.UserName)
	}

	t.Log("Updated user name from: " + globalConfig.UserName + " to: " + globalConfig.NewName)
}
