package main

import (
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"testing"
	"time"

	v4 "github.com/aws/aws-sdk-go-v2/aws/signer/v4"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type S3PresignGetObjectImpl struct{}

func (dt S3PresignGetObjectImpl) PresignGetObject(
	ctx context.Context,
	params *s3.GetObjectInput,
	optFns ...func(*s3.PresignOptions)) (*v4.PresignedHTTPRequest, error) {

	/* The URL looks like (all on one line, no spaces):
	   https://BUCKET-NAME.s3.REGION.amazonaws.com/KEY?
	   X-Amz-Algorithm=AWS4-HMAC-SHA256
	   &X-Amz-Credential=CREDENTIALSREGION%2Fs3%2Faws4_request
	   &X-Amz-Date=20210104T220556Z
	   &X-Amz-Expires=900
	   &X-Amz-SignedHeaders=host
	   &x-id=GetObject
	   &X-Amz-Signature=91cfe149f6457ca7622515c39259a93b62a8e04ab19e775792cbb8c37d13f025
	*/

	// So we can get the region:
	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		panic("configuration error, " + err.Error())
	}

	region := cfg.Region
	string64 := "1234567890123456789012345678901234567890123456789012345678901234"
	url := "https://" +
		*params.Bucket +
		".s3." + region +
		".amazonaws.com/" +
		*params.Key +
		"?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=CREDENTIALS" +
		region +
		"%2Fs3%2Faws4_request&X-Amz-Date=20210104T220556Z&X-Amz-Expires=900&X-Amz-SignedHeaders=host&x-id=GetObject&X-Amz-Signature=" + string64

	output := &v4.PresignedHTTPRequest{
		URL: url,
	}

	return output, nil
}

type Config struct {
	Bucket string `json:"Bucket"`
	Key    string `json:"Key"`
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

	if globalConfig.Bucket == "" || globalConfig.Key == "" {
		msg := "You musts supply a value for Bucket and Key in " + configFileName
		return errors.New(msg)
	}

	return nil
}

func TestPresignGetObject(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration()
	if err != nil {
		t.Fatal(err)
	}

	api := &S3PresignGetObjectImpl{}

	input := &s3.GetObjectInput{
		Bucket: &globalConfig.Bucket,
		Key:    &globalConfig.Key,
	}

	resp, err := GetPresignedURL(context.Background(), *api, input)
	if err != nil {
		t.Log("Got an error ...:")
		t.Log(err)
		return
	}

	t.Log("URL:")
	t.Log(resp.URL)
}
