package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"strconv"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/iam"
	"github.com/aws/aws-sdk-go-v2/service/iam/types"
)

type IAMListAccessKeysImpl struct{}

func (dt IAMListAccessKeysImpl) ListAccessKeys(ctx context.Context,
	params *iam.ListAccessKeysInput,
	optFns ...func(*iam.Options)) (*iam.ListAccessKeysOutput, error) {

	metaData := []types.AccessKeyMetadata{
		{
			AccessKeyId: aws.String("aws-docs-example-access1"),
			Status:      types.StatusTypeActive,
		},
		{
			AccessKeyId: aws.String("aws-docs-example-access2"),
			Status:      types.StatusTypeInactive,
		},
	}

	output := &iam.ListAccessKeysOutput{
		AccessKeyMetadata: metaData,
	}

	return output, nil
}

type Config struct {
	UserName      string `json:"UserName"`
	MaxKeysString string `json:"MaxKeys"`
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

	if globalConfig.UserName == "" || globalConfig.MaxKeysString == "" {
		msg := "You must supply a value for UserName and MaxKeys in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestListAccessKeys(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &IAMListAccessKeysImpl{}

	maxKeys, err := strconv.Atoi(globalConfig.MaxKeysString)
	if err != nil {
		fmt.Println("The MaxKeys value in " + configFileName + "(" + globalConfig.MaxKeysString + ") is not an integer")
		return
	}

	input := &iam.ListAccessKeysInput{
		MaxItems: aws.Int32(int32(maxKeys)),
		UserName: &globalConfig.UserName,
	}

	result, err := GetAccessKeys(context.Background(), api, input)
	if err != nil {
		fmt.Println("Got an error retrieving user access keys:")
		fmt.Println(err)
		return
	}

	for _, key := range result.AccessKeyMetadata {
		fmt.Println("Status for access key " + *key.AccessKeyId + ": " + string(key.Status))
	}
}
