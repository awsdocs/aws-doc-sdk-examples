package main

import (
	"context"
	"encoding/json"
	"io/ioutil"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

type DynamoDBDescribeTableImpl struct{}

func (dt DynamoDBDescribeTableImpl) DescribeTable(ctx context.Context,
	params *dynamodb.DescribeTableInput,
	optFns ...func(*dynamodb.Options)) (*dynamodb.DescribeTableOutput, error) {
	desc := &types.TableDescription{
		ItemCount:      int64(1),
		TableSizeBytes: int64(64),
		TableStatus:    "Active",
	}

	output := &dynamodb.DescribeTableOutput{
		Table: desc,
	}

	return output, nil
}

type Config struct {
	Table string `json:"Table"`
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

	t.Log("Table: " + globalConfig.Table)

	return nil
}

func TestDescribeTable(t *testing.T) {
	thisTime := time.Now()
	nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
	t.Log("Starting unit test at " + nowString)

	err := populateConfiguration(t)
	if err != nil {
		t.Fatal(err)
	}

	if globalConfig.Table == "" {
		t.Fatal("You must set a value for Table in " + configFileName)
	}

	input := dynamodb.DescribeTableInput{
		TableName: &globalConfig.Table,
	}

	api := &DynamoDBDescribeTableImpl{}

	resp, err := GetTableInfo(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error retrieving the table status:")
		t.Log(err)
		return
	}

	t.Log("Info about " + globalConfig.Table + ":")
	t.Log("  #items:     ", resp.Table.ItemCount)
	t.Log("  Size (bytes)", resp.Table.TableSizeBytes)
	t.Log("  Status:     ", string(resp.Table.TableStatus))
}
