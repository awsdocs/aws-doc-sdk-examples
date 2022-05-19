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

type IAMAttachRolePolicyImpl struct{}

func (dt IAMAttachRolePolicyImpl) AttachRolePolicy(ctx context.Context,
	params *iam.AttachRolePolicyInput,
	optFns ...func(*iam.Options)) (*iam.AttachRolePolicyOutput, error) {

	// PolicyArn
	// RoleName
	if nil == params.PolicyArn || *params.PolicyArn == "" || nil == params.RoleName || *params.RoleName == "" {
		args := "PolicyArn or RoleName is nil or an empty string"
		return nil, errors.New(args)
	}

	output := &iam.AttachRolePolicyOutput{}

	return output, nil
}

type Config struct {
	RoleName string `json:"RoleName"`
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

	if globalConfig.RoleName == "" {
		msg := "You must supply a value for RoleName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestAttachRolePolicy(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMAttachRolePolicyImpl{}

	policyArn := "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

	input := &iam.AttachRolePolicyInput{
		PolicyArn: &policyArn,
		RoleName:  &globalConfig.RoleName,
	}

	_, err = AttachDynamoFullPolicy(context.Background(), api, input)
	if err != nil {
		t.Log("Unable to attach DynamoDB full-access role policy to role")
		return
	}

	t.Log("DynamoDB full-access role policy attached to role " + globalConfig.RoleName)
}
