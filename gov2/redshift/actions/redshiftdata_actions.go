// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"errors"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/redshiftdata"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"log"
)

// RedshiftDataActions wraps RedshiftData actions.
type RedshiftDataActions struct {
	RedshiftDataClient *redshiftdata.Client
}

// snippet-start:[gov2.redshift.RedshiftQuery.struct]

// RedshiftQuery makes it easier to deal with RedshiftQuery objects.
type RedshiftQuery struct {
	Result  interface{}
	Input   redshiftdata.DescribeStatementInput
	Context context.Context
}

// snippet-end:[gov2.redshift.RedshiftQuery.struct]

// snippet-start:[gov2.redshift.ExecuteStatement

// ExecuteStatement calls the ExecuteStatement operation from the RedshiftDataClient
func (actor RedshiftDataActions) ExecuteStatement(ctx context.Context, input redshiftdata.ExecuteStatementInput) (*redshiftdata.ExecuteStatementOutput, error) {

	return actor.RedshiftDataClient.ExecuteStatement(ctx, &input)

}

// snippet-end:[gov2.redshift.ExecuteStatement

// snippet-start:[gov2.redshift.ExecuteBatchStatement

// ExecuteBatchStatement calls the BatchExecuteStatement operation from the RedshiftDataClient
func (actor RedshiftDataActions) ExecuteBatchStatement(ctx context.Context, input redshiftdata.BatchExecuteStatementInput) (*redshiftdata.BatchExecuteStatementOutput, error) {
	return actor.RedshiftDataClient.BatchExecuteStatement(ctx, &input)
}

// snippet-end:[gov2.redshift.ExecuteBatchStatement

// snippet-start:[gov2.redshift.ListDatabases]

// ListDatabases lists all databases in the given cluster.
func (actor RedshiftDataActions) ListDatabases(ctx context.Context, clusterId string, databaseName string, userName string) error {
	input := redshiftdata.ListDatabasesInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
	}

	output, err := actor.RedshiftDataClient.ListDatabases(ctx, &input)
	if err != nil {
		log.Printf("Failed to list databases: %v\n", err)
		return err
	}

	for _, database := range output.Databases {
		log.Printf("The database name is : %s\n", database)
	}
	return nil
}

// snippet-end:[gov2.redshift.ListDatabases]

// snippet-start:[gov2.redshift.CreateTable]

// CreateTable creates a table named <tableName> in the <databaseName> database with the given arguments.
func (actor RedshiftDataActions) CreateTable(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, args []string) error {
	sql := "CREATE TABLE " + tableName + " (" +
		"id bigint identity(1, 1), " +
		"PRIMARY KEY (id)"
	for _, value := range args {
		sql += ", " + value
	}
	sql += ");"
	createTableInput := &redshiftdata.ExecuteStatementInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
		Sql:               aws.String(sql),
	}

	output, err := actor.RedshiftDataClient.ExecuteStatement(ctx, createTableInput)
	if err != nil {
		log.Printf("Failed to create table: %v\n", err)
		return err
	}

	log.Println("Table created:", *output.Id)
	return nil
}

// snippet-end:[gov2.redshift.CreateTable]

// snippet-start:[gov2.redshift.DeleteTable]

// DeleteTable drops the table named <tableName> from the <databaseName> database.
func (actor RedshiftDataActions) DeleteTable(ctx context.Context, clusterId string, databaseName string, tableName string, userName string) (bool, error) {
	sql := "DROP TABLE " + tableName
	deleteTableInput := &redshiftdata.ExecuteStatementInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
		Sql:               aws.String(sql),
	}

	output, err := actor.RedshiftDataClient.ExecuteStatement(ctx, deleteTableInput)
	if err != nil {
		log.Printf("Failed to delete table "+tableName+" from "+databaseName+" database: %v\n", err)
		return false, err
	}

	log.Println(tableName+"  table deleted from "+databaseName+" database:", *output.Id)
	return true, nil
}

// snippet-end:[gov2.redshift.DeleteTable]

// snippet-start:[gov2.redshift.DeleteRows]

// DeleteDataRows deletes all rows from the given table.
func (actor RedshiftDataActions) DeleteDataRows(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, pauser demotools.IPausable) (bool, error) {
	deleteRows := &redshiftdata.ExecuteStatementInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
		Sql:               aws.String("DELETE FROM " + tableName + ";"),
	}

	result, err := actor.RedshiftDataClient.ExecuteStatement(ctx, deleteRows)
	if err != nil {
		log.Printf("Failed to execute batch statement: %v\n", err)
		return false, err
	}
	describeInput := redshiftdata.DescribeStatementInput{
		Id: result.Id,
	}
	query := RedshiftQuery{
		Context: ctx,
		Result:  result,
		Input:   describeInput,
	}
	err = actor.WaitForQueryStatus(query, pauser, true)
	if err != nil {
		log.Printf("Failed to execute delete query: %v\n", err)
		return false, err
	}

	log.Printf("Successfully executed delete statement\n")
	return true, nil
}

// snippet-end:[gov2.redshift.DeleteRows]

// snippet-start:[gov2.redshift.WaitForQueryStatus]

// WaitForQueryStatus waits until the given RedshiftQuery object has succeeded or failed.
func (actor RedshiftDataActions) WaitForQueryStatus(query RedshiftQuery, pauser demotools.IPausable, showProgress bool) error {
	done := false
	attempts := 0
	maxWaitCycles := 30
	for done == false {
		// snippet-start:[gov2.redshift.DescribeStatement]
		describeOutput, err := actor.RedshiftDataClient.DescribeStatement(query.Context, &query.Input)
		if err != nil {
			return err
		}
		if describeOutput.Status == "FAILED" {
			return errors.New("failed to describe statement")
		}
		if attempts >= maxWaitCycles {
			return errors.New("timed out waiting for statement")
		}
		if showProgress {
			log.Print(".")
		}
		if describeOutput.Status == "FINISHED" {
			done = true
		}
		// snippet-end:[gov2.redshift.DescribeStatement]
		attempts++
		pauser.Pause(attempts)
	}
	return nil
}

// snippet-end:[gov2.redshift.WaitForQueryStatus]

// snippet-start:[gov2.redshift.GetStatementResult]

// GetStatementResult returns the result of the statement with the given id.
func (actor RedshiftDataActions) GetStatementResult(ctx context.Context, statementId string) (*redshiftdata.GetStatementResultOutput, error) {
	getStatementResultOutput, err := actor.RedshiftDataClient.GetStatementResult(ctx, &redshiftdata.GetStatementResultInput{
		Id: aws.String(statementId),
	})
	if err != nil {
		return nil, err
	}
	return getStatementResultOutput, nil
}

// snippet-end:[gov2.redshift.GetStatementResult]
