package main

import (
	"context"
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
)

// DynamoDBDescribeTableAPI defines the interface for DescribeTable function
type DynamoDBDescribeTableAPI interface {
	DescribeTable(ctx context.Context,
		params *dynamodb.DescribeTableInput,
		optFns ...func(*dynamodb.Options)) (*dynamodb.DescribeTableOutput, error)
}

// GetTableInfo retrieves information about the tabl
func GetTableInfo(c context.Context, api DynamoDBDescribeTableAPI, input *dynamodb.DescribeTableInput) (*dynamodb.DescribeTableOutput, error) {
	resp, err := api.DescribeTable(c, input)

	return resp, err
}

func main() {
	table := flag.String("t", "", "The name of the table")
	region := flag.String("r", "us-west-2", "The region")
	flag.Parse()

	if *table == "" {
		fmt.Println("You must specify a table name (-t TABLE)")
		return
	}

	// Using the SDK's default configuration, loading additional config
	// and credentials values from the environment variables, shared
	// credentials, and shared configuration files
	cfg, err := config.LoadDefaultConfig(config.WithRegion(*region))
	if err != nil {
		panic("unable to load SDK config, " + err.Error())
	}

	// Using the Config value, create the DynamoDB client
	// Create a new DynamoDB Service Client
	client := dynamodb.NewFromConfig(cfg)

	// Build the request with its input parameters
	resp, err := client.DescribeTable(context.Background(), &dynamodb.DescribeTableInput{
		TableName: table,
	})
	if err != nil {
		panic("failed to describe table, " + err.Error())
	}

	fmt.Println("Info about " + *table + ":")
	fmt.Println("  #items:     ", *resp.Table.ItemCount)
	fmt.Println("  Size (bytes)", *resp.Table.TableSizeBytes)
	fmt.Println("  Status:     ", string(resp.Table.TableStatus))
}
