// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gov2.redshift.BasicsScenario]

package scenarios

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"math/rand"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	redshift_types "github.com/aws/aws-sdk-go-v2/service/redshift/types"
	redshiftdata_types "github.com/aws/aws-sdk-go-v2/service/redshiftdata/types"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/redshift/actions"

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
	secretsmanager    *SecretsManager
}

// SecretsManager is used to retrieve username and password information from a secure service.
type SecretsManager struct {
	SecretsManagerClient *secretsmanager.Client
}

// RedshiftBasics constructs a new Redshift Basics runner.
func RedshiftBasics(sdkConfig aws.Config, questioner demotools.IQuestioner, pauser demotools.IPausable, filesystem demotools.IFileSystem, helper IScenarioHelper) RedshiftBasicsScenario {
	scenario := RedshiftBasicsScenario{
		sdkConfig:         sdkConfig,
		helper:            helper,
		questioner:        questioner,
		pauser:            pauser,
		filesystem:        filesystem,
		secretsmanager:    &SecretsManager{SecretsManagerClient: secretsmanager.NewFromConfig(sdkConfig)},
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

// User makes it easier to get the User data back from SecretsManager and use it later.
type User struct {
	Username string `json:"userName"`
	Password string `json:"userPassword"`
}

// Run runs the RedshiftBasics interactive example that shows you how to use Amazon
// Redshift and how to interact with its common endpoints.
//
// 0. Retrieve username and password information to access Redshift.
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
func (runner *RedshiftBasicsScenario) Run(ctx context.Context) {

	user := User{}
	secretId := "s3express/basics/secrets"
	clusterId := "demo-cluster-1"
	maintenanceWindow := "wed:07:30-wed:08:00"
	databaseName := "dev"
	tableName := "Movies"
	fileName := "Movies.json"
	nodeType := "ra3.xlplus"
	clusterType := "single-node"

	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
			_, isMock := runner.questioner.(*demotools.MockQuestioner)
			if isMock || runner.questioner.AskBool("Do you want to see the full error message (y/n)?", "y") {
				log.Println(r)
			}
			runner.cleanUpResources(ctx, clusterId, databaseName, tableName, user.Username, runner.questioner)
		}
	}()

	// Retrieve the userName and userPassword from SecretsManager
	output, err := runner.secretsmanager.SecretsManagerClient.GetSecretValue(ctx, &secretsmanager.GetSecretValueInput{
		SecretId: aws.String(secretId),
	})
	if err != nil {
		log.Printf("There was a problem getting the secret value: %s", err)
		log.Printf("Please make sure to create a secret named 's3express/basics/secrets' with keys of 'userName' and 'userPassword'.")
		panic(err)
	}

	err = json.Unmarshal([]byte(*output.SecretString), &user)
	if err != nil {
		log.Printf("There was a problem parsing the secret value from JSON: %s", err)
		panic(err)
	}

	// Create the Redshift cluster
	_, err = runner.redshiftActor.CreateCluster(ctx, clusterId, user.Username, user.Password, nodeType, clusterType, true)
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
	waiter := redshift.NewClusterAvailableWaiter(runner.redshiftActor.RedshiftClient)
	err = waiter.Wait(ctx, &redshift.DescribeClustersInput{
		ClusterIdentifier: aws.String(clusterId),
	}, 5*time.Minute)
	if err != nil {
		log.Println("An error occurred waiting for the cluster.")
		panic(err)
	}

	// Get some info about the cluster
	describeOutput, err := runner.redshiftActor.DescribeClusters(ctx, clusterId)
	if err != nil {
		log.Println("Something went wrong trying to get information about the cluster.")
		panic(err)
	}
	log.Println("Here's some information about the cluster.")
	log.Printf("The cluster's status is %s", *describeOutput.Clusters[0].ClusterStatus)
	log.Printf("The cluster was created at %s", *describeOutput.Clusters[0].ClusterCreateTime)

	// List databases
	log.Println("List databases in", clusterId)
	runner.questioner.Ask("Press Enter to continue...")
	err = runner.redshiftDataActor.ListDatabases(ctx, clusterId, databaseName, user.Username)
	if err != nil {
		log.Printf("Failed to list databases: %v\n", err)
		panic(err)
	}

	// Create the "Movies" table
	log.Println("Now you will create a table named " + tableName + ".")
	runner.questioner.Ask("Press Enter to continue...")
	err = nil
	result, err := runner.redshiftDataActor.CreateTable(ctx, clusterId, databaseName, tableName, user.Username, runner.pauser, []string{"title VARCHAR(256)", "year INT"})
	if err != nil {
		log.Printf("Failed to create table: %v\n", err)
		panic(err)
	}

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
		panic(err)
	}
	log.Printf("Successfully executed query\n")

	// Populate the "Movies" table
	runner.PopulateMoviesTable(ctx, clusterId, databaseName, tableName, user.Username, fileName)

	// Query the "Movies" table by year
	log.Println("Query the Movies table by year.")
	year := runner.questioner.AskInt(
		fmt.Sprintf("Enter a value between %v and %v:", 2012, 2014),
		demotools.InIntRange{Lower: 2012, Upper: 2014})
	runner.QueryMoviesByYear(ctx, clusterId, databaseName, tableName, user.Username, year)

	// Modify the cluster's maintenance window
	runner.redshiftActor.ModifyCluster(ctx, clusterId, maintenanceWindow)

	// Delete the Redshift cluster if confirmed
	runner.cleanUpResources(ctx, clusterId, databaseName, tableName, user.Username, runner.questioner)

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
		panic(err)
	}

	var sqlStatements []string

	for i, movie := range movies {
		if i >= numRecords {
			break
		}

		sqlStatement := fmt.Sprintf(`INSERT INTO %s (title, year) VALUES ('%s', %d);`,
			tableName,
			strings.Replace(movie.Title, "'", "''", -1), // Double any single quotes to escape them
			movie.Year)

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
		panic(err)
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
		panic(err)
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
		panic(err)
	}
	log.Printf("Successfully executed query\n")

	getResultOutput, err := runner.redshiftDataActor.GetStatementResult(ctx, *result.Id)
	if err != nil {
		log.Printf("Failed to query movies: %v\n", err)
		panic(err)
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

// snippet-end:[gov2.redshift.BasicsScenario]
