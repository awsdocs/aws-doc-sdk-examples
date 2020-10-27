package main

import (
	"context"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"

	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

type DynamoDBDescribeTableImpl struct{}

func (dt DynamoDBDescribeTableImpl) DescribeTable(ctx context.Context,
	params *dynamodb.DescribeTableInput,
	optFns ...func(*dynamodb.Options)) (*dynamodb.DescribeTableOutput, error) {
	desc := &types.TableDescription{
		ItemCount:      aws.Int64(1),
		TableSizeBytes: aws.Int64(64),
		TableStatus:    "Active",
	}

	output := &dynamodb.DescribeTableOutput{
		Table: desc,
	}

	return output, nil
}

func TestDescribeTable(t *testing.T) {
	tableName := "MyGroovyTable"

	// Build the request with its input parameters
	input := dynamodb.DescribeTableInput{
		TableName: &tableName,
	}

	api := &DynamoDBDescribeTableImpl{}

	resp, err := GetTableInfo(context.Background(), *api, &input)
	if err != nil {
		t.Log("Got an error retrieving the table status:")
		t.Log(err)
		return
	}

	t.Log("Info about " + tableName + ":")
	t.Log("  #items:     ", *resp.Table.ItemCount)
	t.Log("  Size (bytes)", *resp.Table.TableSizeBytes)
	t.Log("  Status:     ", string(resp.Table.TableStatus))
}
