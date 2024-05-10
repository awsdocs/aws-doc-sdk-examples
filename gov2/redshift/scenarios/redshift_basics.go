// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/aws/aws-sdk-go-v2/aws"
	redshift_types "github.com/aws/aws-sdk-go-v2/service/redshift/types"
	redshiftdata_types "github.com/aws/aws-sdk-go-v2/service/redshiftdata/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/redshift/actions"
	"log"
	"math/rand"
	"time"

	"github.com/aws/aws-sdk-go-v2/service/redshift"
	"github.com/aws/aws-sdk-go-v2/service/redshiftdata"
)

// IScenarioHelper abstracts input and wait functions from a scenario so that they
// can be mocked for unit testing.
type IScenarioHelper interface {
	GetName() string
}

const rMax = 100000

type ScenarioHelper struct {
	Prefix string
	Random *rand.Rand
}

// GetName returns a unique name formed of a prefix and a random number.
func (helper ScenarioHelper) GetName() string {
	return fmt.Sprintf("%v%v", helper.Prefix, helper.Random.Intn(rMax))
}

// RedshiftBasicsScenario separates the steps of this scenario into individual functions so that
// they are simpler to read and understand.
type RedshiftBasicsScenario struct {
	sdkConfig         aws.Config
	helper            IScenarioHelper
	questioner        demotools.IQuestioner
	pauser            demotools.IPausable
	filesystem        demotools.IFileSystem
	redshiftActor     *actions.RedshiftActions
	redshiftDataActor *actions.RedshiftDataActions
}

// RedshiftBasics constructs a new Redshift Basics runner.
func RedshiftBasics(sdkConfig aws.Config, questioner demotools.IQuestioner, pauser demotools.IPausable, filesystem demotools.IFileSystem, helper IScenarioHelper) RedshiftBasicsScenario {
	scenario := RedshiftBasicsScenario{
		sdkConfig:         sdkConfig,
		helper:            helper,
		questioner:        questioner,
		pauser:            pauser,
		filesystem:        filesystem,
		redshiftActor:     &actions.RedshiftActions{RedshiftClient: redshift.NewFromConfig(sdkConfig)},
		redshiftDataActor: &actions.RedshiftDataActions{RedshiftDataClient: redshiftdata.NewFromConfig(sdkConfig)},
	}
	return scenario
}

// snippet-start:[gov2.redshift.Movie.struct]

// Movie makes it easier to use Movie objects given in json format.
type Movie struct {
	ID    int    `json:"id"`
	Title string `json:"title"`
	Year  int    `json:"year"`
}

// snippet-end:[gov2.redshift.Movie.struct]

// snippet-start:[gov2.redshift.BasicsScenario]

// Run runs the RedshiftBasics interactive example that shows you how to use Amazon
// Redshift and how to interact with its common endpoints.
//
// 1. Create a cluster.
// 2. Wait for the cluster to become available.
// 3. List the available databases in the region.
// 4. Create a table named "Movies" in the "dev" database.
// 5. Populate the movies table from the "movies.json" file.
// 6. Query the movies table by year.
// 7. Modify the cluster's maintenance window.
// 8. Optionally clean up all resources created during this demo.
//
// This example creates an Amazon Redshift service client from the specified sdkConfig so that
// you can replace it with a mocked or stubbed config for unit testing.
//
// It uses a questioner from the `demotools` package to get input during the example.
// This package can be found in the ..\..\demotools folder of this repo.
func (runner *RedshiftBasicsScenario) Run() {

	// Prompt the user for input
	userName, userPassword := "awsuser", "AwsUser1000"
	clusterId := "test-cluster-1"
	databaseName := "dev"
	tableName := "Movies"
	fileName := "Movies.json"
	runner.helper.GetName()
	ctx := context.TODO()

	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
			_, isMock := runner.questioner.(*demotools.MockQuestioner)
			if isMock || runner.questioner.AskBool("Do you want to see the full error message (y/n)?", "y") {
				log.Println(r)
			}
			runner.cleanUpResources(ctx, clusterId, databaseName, tableName, userName, runner.questioner)
		}
	}()

	// Create the Redshift cluster
	nodeType := "ra3.xlplus"
	clusterType := "single-node"
	_, err := runner.redshiftActor.CreateCluster(ctx, clusterId, userName, userPassword, nodeType, clusterType, true)
	if err != nil {
		var clusterAlreadyExistsFault *redshift_types.ClusterAlreadyExistsFault
		if errors.As(err, &clusterAlreadyExistsFault) {
			log.Println("Cluster already exists. Continuing.")
		} else {
			log.Println("Error creating cluster.")
			panic(err)
		}
	}

	// Wait for the cluster to become available
	err = runner.WaitForClusterAvailable(runner.redshiftActor.RedshiftClient, clusterId, runner.questioner, runner.pauser)
	if err != nil {
		log.Println("An error occurred waiting for the cluster.")
		panic(err)
	}

	// List databases
	log.Println("List databases in", clusterId)
	runner.questioner.Ask("Press Enter to continue...")
	err = runner.redshiftDataActor.ListDatabases(ctx, clusterId, databaseName, userName)
	if err != nil {
		log.Printf("Failed to list databases: %v\n", err)
		panic(err)
	}

	// Create the "Movies" table
	log.Println("Now you will create a table named " + tableName + ".")
	runner.questioner.Ask("Press Enter to continue...")
	err = runner.redshiftDataActor.CreateTable(ctx, clusterId, databaseName, tableName, userName, []string{"title VARCHAR(256)", "year INT"})
	if err != nil {
		log.Printf("Failed to create table: %v\n", err)
		panic(err)
	}

	// Populate the "Movies" table
	runner.PopulateMoviesTable(ctx, clusterId, databaseName, tableName, userName, fileName)

	// Query the "Movies" table by year
	log.Println("Query the Movies table by year.")
	year := runner.questioner.AskInt(
		fmt.Sprintf("Enter a value between %v and %v:", 2012, 2014),
		demotools.InIntRange{Lower: 2012, Upper: 2014})
	runner.QueryMoviesByYear(ctx, clusterId, databaseName, tableName, userName, year)

	// Modify the cluster's maintenance window
	//maintenanceWindow := aws.String("wed:07:30-wed:08:00")
	runner.redshiftActor.ModifyCluster(ctx, clusterId, "wed:07:30-wed:08:00")

	// Delete the Redshift cluster if confirmed
	runner.cleanUpResources(ctx, clusterId, databaseName, tableName, userName, runner.questioner)

	log.Println("Thanks for watching!")
}

// cleanUpResources asks the user if they would like to delete each resource created during the scenario, from most
// impactful to least impactful. If any choice to delete is made, further deletion attempts are skipped.
func (runner *RedshiftBasicsScenario) cleanUpResources(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, questioner demotools.IQuestioner) {
	deleted := false
	var err error = nil
	if questioner.AskBool("Do you want to delete the entire cluster? This will clean up all resources. (y/n)", "y") {
		deleted, err = runner.redshiftActor.DeleteCluster(ctx, clusterId)
		if err != nil {
			log.Printf("Error deleting cluster: %v", err)
		}
	}
	if !deleted && questioner.AskBool("Do you want to delete the dev table? This will clean up all inserted records but keep your cluster intact. (y/n)", "y") {
		deleted, err = runner.redshiftDataActor.DeleteTable(ctx, clusterId, databaseName, tableName, userName)
		if err != nil {
			log.Printf("Error deleting movies table: %v", err)
		}
	}
	if !deleted && questioner.AskBool("Do you want to delete all rows in the Movies table? This will clean up all inserted records but keep your cluster and table intact. (y/n)", "y") {
		deleted, err = runner.redshiftDataActor.DeleteDataRows(ctx, clusterId, databaseName, tableName, userName, runner.pauser)
		if err != nil {
			log.Printf("Error deleting data rows: %v", err)
		}
	}
	if !deleted {
		log.Print("Please manually delete any unwanted resources.")
	}
}

// snippet-end:[gov2.redshift.BasicsScenario]

// snippet-start:[gov2.redshift.loadMoviesFromJSON]

// loadMoviesFromJSON takes the <fileName> file and populates a slice of Movie objects.
func (runner *RedshiftBasicsScenario) loadMoviesFromJSON(fileName string, filesystem demotools.IFileSystem) ([]Movie, error) {
	file, err := filesystem.OpenFile("../../resources/sample_files/" + fileName)
	if err != nil {
		return nil, err
	}
	defer filesystem.CloseFile(file)

	var movies []Movie
	err = json.NewDecoder(file).Decode(&movies)
	if err != nil {
		return nil, err
	}

	return movies, nil
}

// snippet-end:[gov2.redshift.loadMoviesFromJSON]

// snippet-start:[gov2.redshift.WaitForClusterAvailable]

// WaitForClusterAvailable loops until a success or failure state is returned about the given cluster.
func (runner *RedshiftBasicsScenario) WaitForClusterAvailable(client *redshift.Client, clusterId string, questioner demotools.IQuestioner, pauser demotools.IPausable) error {
	questioner.Ask("Now we will wait until the cluster is available. Press Enter to continue...")

	log.Println("Waiting for cluster to become available. This may take a few minutes.")
	describeClusterInput := &redshift.DescribeClustersInput{
		ClusterIdentifier: &clusterId,
	}

	for {
		// snippet-start:[gov2.redshift.DescribeClusters]
		output, err := client.DescribeClusters(context.TODO(), describeClusterInput)
		if err != nil {
			return err
		}

		if output.Clusters[0].ClusterStatus != nil && *output.Clusters[0].ClusterStatus == "available" {
			log.Println("Cluster is available! Total Elapsed Time:", time.Since(time.Now().Add(-1*time.Second*time.Duration(output.Clusters[0].ClusterCreateTime.Second()))))
			return nil
		}

		log.Print("Elapsed Time: ")
		log.Println(time.Since(time.Now().Add(-1 * time.Second * time.Duration(output.Clusters[0].ClusterCreateTime.Second()))))
		// snippet-end:[gov2.redshift.DescribeClusters]

		pauser.Pause(5)
	}
}

// snippet-end:[gov2.redshift.WaitForClusterAvailable]

// snippet-start:[gov2.redshift.PopulateMoviesTable]

// PopulateMoviesTable reads data from the <fileName> file and inserts records into the "Movies" table.
func (runner *RedshiftBasicsScenario) PopulateMoviesTable(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, fileName string) {
	log.Println("Populate the " + tableName + " table using the " + fileName + " file.")
	numRecords := runner.questioner.AskInt(
		fmt.Sprintf("Enter a value between %v and %v:", 10, 100),
		demotools.InIntRange{Lower: 10, Upper: 100})

	movies, err := runner.loadMoviesFromJSON(fileName, runner.filesystem)
	if err != nil {
		log.Printf("Failed to load movies from JSON: %v\n", err)
		return
	}

	var sqlStatements []string

	for i, movie := range movies {
		if i >= numRecords {
			break
		}

		sqlStatement := fmt.Sprintf(`INSERT INTO %s (title, year) VALUES ('%s', %d);`, tableName, movie.Title, movie.Year)
		sqlStatements = append(sqlStatements, sqlStatement)
	}

	input := &redshiftdata.BatchExecuteStatementInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
		Sqls:              sqlStatements,
	}

	result, err := runner.redshiftDataActor.ExecuteBatchStatement(ctx, *input)
	if err != nil {
		log.Printf("Failed to execute batch statement: %v\n", err)
		return
	}

	describeInput := redshiftdata.DescribeStatementInput{
		Id: result.Id,
	}

	query := actions.RedshiftQuery{
		Context: ctx,
		Result:  result,
		Input:   describeInput,
	}
	err = runner.redshiftDataActor.WaitForQueryStatus(query, runner.pauser, true)
	if err != nil {
		log.Printf("Failed to execute batch insert query: %v\n", err)
		return
	}
	log.Printf("Successfully executed batch statement\n")

	log.Printf("%d records were added to the Movies table.\n", numRecords)
}

// snippet-end:[gov2.redshift.PopulateMoviesTable]

// snippet-start:[gov2.redshift.QueryMoviesByYear]

// QueryMoviesByYear retrieves only movies from the "Movies" table which match the given year.
func (runner *RedshiftBasicsScenario) QueryMoviesByYear(ctx context.Context, clusterId string, databaseName string, tableName string, userName string, year int) {

	sqlStatement := fmt.Sprintf(`SELECT title FROM %s WHERE year = %d;`, tableName, year)

	input := &redshiftdata.ExecuteStatementInput{
		ClusterIdentifier: aws.String(clusterId),
		Database:          aws.String(databaseName),
		DbUser:            aws.String(userName),
		Sql:               aws.String(sqlStatement),
	}

	result, err := runner.redshiftDataActor.ExecuteStatement(ctx, *input)
	if err != nil {
		log.Printf("Failed to query movies: %v\n", err)
		return
	}

	log.Println("The identifier of the statement is ", *result.Id)

	describeInput := redshiftdata.DescribeStatementInput{
		Id: result.Id,
	}

	query := actions.RedshiftQuery{
		Context: ctx,
		Input:   describeInput,
		Result:  result,
	}
	err = runner.redshiftDataActor.WaitForQueryStatus(query, runner.pauser, true)
	if err != nil {
		log.Printf("Failed to execute query: %v\n", err)
		return
	}
	log.Printf("Successfully executed query\n")

	getResultOutput, err := runner.redshiftDataActor.GetStatementResult(ctx, *result.Id)
	if err != nil {
		log.Printf("Failed to query movies: %v\n", err)
		return
	}
	for _, row := range getResultOutput.Records {
		for _, col := range row {
			title, ok := col.(*redshiftdata_types.FieldMemberStringValue)
			if !ok {
				log.Println("Failed to parse the field")
			} else {
				log.Printf("The Movie title field is %s\n", title.Value)
			}
		}
	}
}

// snippet-end:[gov2.redshift.QueryMoviesByYear]
