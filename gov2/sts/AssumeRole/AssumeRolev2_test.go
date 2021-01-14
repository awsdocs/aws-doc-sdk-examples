package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/aws/aws-sdk-go-v2/service/sts/types"
	"github.com/aws/aws-sdk-go/aws"
)

type STSAssumeRoleImpl struct{}

func (dt STSAssumeRoleImpl) AssumeRole(ctx context.Context,
	params *sts.AssumeRoleInput,
	optFns ...func(*sts.Options)) (*sts.AssumeRoleOutput, error) {

	user := types.AssumedRoleUser{
		Arn:           aws.String("aws-docs-example-user-arn"),
		AssumedRoleId: aws.String("aws-docs-example-userID"),
	}

	output := &sts.AssumeRoleOutput{
		AssumedRoleUser: &user,
	}

	return output, nil
}

type Config struct {
	RoleArn     string `json:"RoleArn"`
	SessionName string `json:"SessionName"`
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

	if globalConfig.RoleArn == "" || globalConfig.SessionName == "" {
		msg := "You must specify a value for RoleArn and SessionName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestAssumeRole(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	api := &STSAssumeRoleImpl{}

	input := &sts.AssumeRoleInput{
		RoleArn:         &globalConfig.RoleArn,
		RoleSessionName: &globalConfig.SessionName,
	}

	resp, err := TakeRole(context.TODO(), api, input)
	if err != nil {
		fmt.Println("Got an error assuming the role:")
		fmt.Println(err)
		return
	}

	t.Log("User ARN:      " + *resp.AssumedRoleUser.Arn)
	t.Log("User role ID:  " + *resp.AssumedRoleUser.AssumedRoleId)
}
