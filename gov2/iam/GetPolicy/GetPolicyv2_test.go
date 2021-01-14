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
	"github.com/aws/aws-sdk-go/aws"
)

type IAMGetPolicyImpl struct{}

func (dt IAMGetPolicyImpl) GetPolicy(ctx context.Context,
	params *iam.GetPolicyInput,
	optFns ...func(*iam.Options)) (*iam.GetPolicyOutput, error) {

	policy := types.Policy{
		Description: aws.String("aws-docs-example-policy-description"),
	}

	output := &iam.GetPolicyOutput{
		Policy: &policy,
	}

	return output, nil
}

type Config struct {
	PolicyARN string `json:"PolicyARN"`
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

	if globalConfig.PolicyARN == "" {
		msg := "You must supply a value for PolicyARN in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestGetPolicy(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMGetPolicyImpl{}

	input := &iam.GetPolicyInput{
		PolicyArn: &globalConfig.PolicyARN,
	}

	result, err := GetPolicyDescription(context.Background(), api, input)
	if err != nil {
		t.Log("Got an error retrieving the description:")
		t.Log(err)
		return
	}

	description := ""

	if nil == result.Policy {
		description = "Policy nil"
	} else {
		if nil == result.Policy.Description {
			description = "Description nil"
		} else {
			description = *result.Policy.Description
		}
	}

	t.Log("Description:")
	t.Log(description)
}
