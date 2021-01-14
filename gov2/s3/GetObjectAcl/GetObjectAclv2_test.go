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
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
)

type S3GetObjectAclImpl struct{}

func (dt S3GetObjectAclImpl) GetObjectAcl(ctx context.Context,
	params *s3.GetObjectAclInput,
	optFns ...func(*s3.Options)) (*s3.GetObjectAclOutput, error) {
	grants := make([]*types.Grant, 1)
	grantee := &types.Grantee{DisplayName: aws.String("theuser")}
	grants[0] = &types.Grant{Grantee: grantee}

	output := &s3.GetObjectAclOutput{
		Grants: grants,
	}

	return output, nil
}

type Config struct {
	BucketName string `json:"BucketName"`
	ObjectName string `json:"ObjectName"`
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

	if globalConfig.BucketName == "" || globalConfig.ObjectName == "" {
		msg := "You must supply a value for BucketName and ObjectName in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestGetObjectAcl(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	// Build the request with its input parameters
	input := s3.GetObjectAclInput{
		Bucket: &globalConfig.BucketName,
		Key:    &globalConfig.ObjectName,
	}

	api := &S3GetObjectAclImpl{}

	resp, err := FindObjectAcl(context.TODO(), *api, &input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("Grantee for object " + globalConfig.ObjectName + ": " + *resp.Grants[0].Grantee.DisplayName)
}
