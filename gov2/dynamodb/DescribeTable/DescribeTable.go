// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.gov2.DescribeTable]
package main

// snippet-start:[dynamodb.gov2.DescribeTable.imports]
import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
)

// snippet-end:[dynamodb.gov2.DescribeTable.imports]

// DynamoDBDescribeTableAPI defines the interface for DescribeTable function.
// We use this interface to enable unit testing.
// snippet-start:[dynamodb.gov2.DescribeTable.interface]
type DynamoDBDescribeTableAPI interface {
	DescribeTable(ctx context.Context,
		params *dynamodb.DescribeTableInput,
		optFns ...func(*dynamodb.Options)) (*dynamodb.DescribeTableOutput, error)
}

// snippet-end:[dynamodb.gov2.DescribeTable.interface]

// GetTableInfo retrieves information about the table.
// snippet-start:[dynamodb.gov2.DescribeTable.GetTableInfo]
func GetTableInfo(c context.Context, api DynamoDBDescribeTableAPI, input *dynamodb.DescribeTableInput) (*dynamodb.DescribeTableOutput, error) {
	resp, err := api.DescribeTable(c, input)

	return resp, err
}

// snippet-end:[dynamodb.gov2.DescribeTable.GetTableInfo]

func main() {
	// snippet-start:[dynamodb.gov2.DescribeTable.args]
	table := flag.String("t", "", "The name of the table")
	flag.Parse()

	if *table == "" {
		fmt.Println("You must specify a table name (-t TABLE)")
		return
	}
	// snippet-end:[dynamodb.gov2.DescribeTable.args]

	// snippet-start:[dynamodb.gov2.DescribeTable.config_and_client]
	// Use the SDK's default configuration.
	cfg, err := config.LoadDefaultConfig()
	if err != nil {
		panic("unable to load SDK config, " + err.Error())
	}

	// Create a DynamoDB client
	// using the default credentials and region
	client := dynamodb.NewFromConfig(cfg)
	// snippet-end:[dynamodb.gov2.DescribeTable.config_and_client]

	// snippet-start:[dynamodb.gov2.DescribeTable.call]
	// Build the request with its input parameters
	input := &dynamodb.DescribeTableInput{
		TableName: table,
	}

	resp, err := GetTableInfo(context.Background(), client, input)
	if err != nil {
		panic("failed to describe table, " + err.Error())
	}
	// snippet-end:[dynamodb.gov2.DescribeTable.call]

	// snippet-start:[dynamodb.gov2.DescribeTable.print]
	fmt.Println("Info about " + *table + ":")
	fmt.Println("  #items:     ", *resp.Table.ItemCount)
	fmt.Println("  Size (bytes)", *resp.Table.TableSizeBytes)
	fmt.Println("  Status:     ", string(resp.Table.TableStatus))
	// snippet-end:[dynamodb.gov2.DescribeTable.print]
}

// snippet-end:[dynamodb.gov2.DescribeTable]
