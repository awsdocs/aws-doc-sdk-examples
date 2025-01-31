// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.redshift.DataActionsStruct]

import (
	"context"
	"errors"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/redshiftdata"
	"github.com/aws/aws-sdk-go-v2/service/redshiftdata/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"log"
)

// RedshiftDataActions wraps RedshiftData actions.
type RedshiftDataActions struct {
	RedshiftDataClient *redshiftdata.Client
}

// snippet-end:[gov2.redshift.DataActionsStruct]

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

	var opErr *types.DatabaseConnectionException
	var databaseNames []string
	paginator := redshiftdata.NewListDatabasesPaginator(actor.RedshiftDataClient, &redshiftdata.ListDatabasesInput{
		Database:          aws.String(databaseName),
		ClusterIdentifier: aws.String(clusterId),
		DbUser:            aws.String(userName),
	})
	for paginator.HasMorePages() {
		output, err := paginator.NextPage(ctx)
		if err != nil && errors.As(err, &opErr) {
			log.Printf("Could not connect to the database.")
			panic(err)
		} else if err != nil {
			log.Printf("Couldn't finish listing the tables. Here's why: %v\n", err)
			return err
		} else {
			databaseNames = append(databaseNames, output.Databases...)
		}
	}

	for _, database := range databaseNames {
		log.Printf("The database name is : %s\n", database)
	}
	return nil
}

// snippet-end:[gov2.redshift.ListDatabases]

// snippet-start:[gov2.redshift.CreateTable]

// CreateTable creates a table named <tableName> in the <databaseName> database with the given arguments.
func (actor RedshiftDataActions) CreateTable(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, pauser demotools.IPausable, args []string) (*redshiftdata.ExecuteStatementOutput, error) {
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

	var opErr *types.DatabaseConnectionException
	output, err := actor.RedshiftDataClient.ExecuteStatement(ctx, createTableInput)
	if err != nil && errors.As(err, &opErr) {
		log.Printf("Could not connect to the database.")
	} else if err != nil {
		log.Printf("Failed to create table: %v\n", err)
		return nil, err
	}

	describeStatementInput := &redshiftdata.DescribeStatementInput{Id: output.Id}
	query := RedshiftQuery{
		Context: ctx,
		Input:   *describeStatementInput,
		Result:  output,
	}

	err = actor.WaitForQueryStatus(query, pauser, true)
	if err != nil {
		log.Printf("Failed to execute query: %v\n", err)
		panic(err)
	}
	log.Printf("Successfully executed query\n")

	log.Println("Table created:", *output.Id)
	return output, nil
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

	var opErr *types.DatabaseConnectionException
	output, err := actor.RedshiftDataClient.ExecuteStatement(ctx, deleteTableInput)
	if err != nil && errors.As(err, &opErr) {
		log.Printf("Could not connect to the database.")
	} else if err != nil {
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

// snippet-start:[gov2.redshift.DescribeStatement]

// DescribeStatement gets information about the given statement.
func (actor RedshiftDataActions) DescribeStatement(query RedshiftQuery) (*redshiftdata.DescribeStatementOutput, error) {
	describeOutput, err := actor.RedshiftDataClient.DescribeStatement(query.Context, &query.Input)
	var opErr *types.QueryTimeoutException
	if errors.As(err, &opErr) {
		println("The connection to the redshift data request timed out.")
		panic(err)
	} else if err != nil {
		println("Failed to execute describe statement")
		return nil, err
	}
	return describeOutput, nil
}

// snippet-end:[gov2.redshift.DescribeStatement]

// snippet-start:[gov2.redshift.WaitForQueryStatus]

// WaitForQueryStatus waits until the given RedshiftQuery object has succeeded or failed.
func (actor RedshiftDataActions) WaitForQueryStatus(query RedshiftQuery, pauser demotools.IPausable, showProgress bool) error {
	done := false
	attempts := 0
	maxWaitCycles := 30
	for done == false {
		describeOutput, err := actor.DescribeStatement(query)
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
		attempts++
		pauser.Pause(attempts)
	}
	return nil
}

// snippet-end:[gov2.redshift.WaitForQueryStatus]

// snippet-start:[gov2.redshift.GetStatementResult]

// GetStatementResult returns the result of the statement with the given id.
func (actor RedshiftDataActions) GetStatementResult(ctx context.Context, statementId string) (*redshiftdata.GetStatementResultOutput, error) {

	var opErr *types.QueryTimeoutException
	getStatementResultOutput, err := actor.RedshiftDataClient.GetStatementResult(ctx, &redshiftdata.GetStatementResultInput{
		Id: aws.String(statementId),
	})
	if err != nil && errors.As(err, &opErr) {
		log.Printf("Query timed out: %v\n", err)
		return nil, err
	} else if err != nil {
		return nil, err
	}
	return getStatementResultOutput, nil
}

// snippet-end:[gov2.redshift.GetStatementResult]
