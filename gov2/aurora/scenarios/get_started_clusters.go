// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
	"aurora/actions"
	"fmt"
	"log"
	"sort"
	"strconv"
	"strings"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/rds"
	"github.com/aws/aws-sdk-go-v2/service/rds/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/google/uuid"
)

// IScenarioHelper abstracts the function from a scenario so that it
// can be mocked for unit testing.
type IScenarioHelper interface {
	Pause(secs int)
	UniqueId() string
}
type ScenarioHelper struct{}

// Pause waits for the specified number of seconds.
func (helper ScenarioHelper) Pause(secs int) {
	time.Sleep(time.Duration(secs) * time.Second)
}

// UniqueId returns a new UUID.
func (helper ScenarioHelper) UniqueId() string {
	return uuid.New().String()
}

// snippet-start:[gov2.aurora.Scenario_GetStartedClusters]

// GetStartedClusters is an interactive example that shows you how to use the AWS SDK for Go
// with Amazon Aurora to do the following:
//
// 1. Create a custom DB cluster parameter group and set parameter values.
// 2. Create an Aurora DB cluster that is configured to use the parameter group.
// 3. Create a DB instance in the DB cluster that contains a database.
// 4. Take a snapshot of the DB cluster.
// 5. Delete the DB instance, DB cluster, and parameter group.
type GetStartedClusters struct {
	sdkConfig  aws.Config
	dbClusters actions.DbClusters
	questioner demotools.IQuestioner
	helper     IScenarioHelper
	isTestRun  bool
}

// NewGetStartedClusters constructs a GetStartedClusters instance from a configuration.
// It uses the specified config to get an Amazon Relational Database Service (Amazon RDS)
// client and create wrappers for the actions used in the scenario.
func NewGetStartedClusters(sdkConfig aws.Config, questioner demotools.IQuestioner,
	helper IScenarioHelper) GetStartedClusters {
	auroraClient := rds.NewFromConfig(sdkConfig)
	return GetStartedClusters{
		sdkConfig:  sdkConfig,
		dbClusters: actions.DbClusters{AuroraClient: auroraClient},
		questioner: questioner,
		helper:     helper,
	}
}

// Run runs the interactive scenario.
func (scenario GetStartedClusters) Run(dbEngine string, parameterGroupName string,
	clusterName string, dbName string) {
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon Aurora DB Cluster demo.")
	log.Println(strings.Repeat("-", 88))

	parameterGroup := scenario.CreateParameterGroup(dbEngine, parameterGroupName)
	scenario.SetUserParameters(parameterGroupName)
	cluster := scenario.CreateCluster(clusterName, dbEngine, dbName, parameterGroup)
	scenario.helper.Pause(5)
	dbInstance := scenario.CreateInstance(cluster)
	scenario.DisplayConnection(cluster)
	scenario.CreateSnapshot(clusterName)
	scenario.Cleanup(dbInstance, cluster, parameterGroup)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// CreateParameterGroup shows how to get available engine versions for a specified
// database engine and create a DB cluster parameter group that is compatible with a
// selected engine family.
func (scenario GetStartedClusters) CreateParameterGroup(dbEngine string,
	parameterGroupName string) *types.DBClusterParameterGroup {

	log.Printf("Checking for an existing DB cluster parameter group named %v.\n",
		parameterGroupName)
	parameterGroup, err := scenario.dbClusters.GetParameterGroup(parameterGroupName)
	if err != nil {
		panic(err)
	}
	if parameterGroup == nil {
		log.Printf("Getting available database engine versions for %v.\n", dbEngine)
		engineVersions, err := scenario.dbClusters.GetEngineVersions(dbEngine, "")
		if err != nil {
			panic(err)
		}

		familySet := map[string]struct{}{}
		for _, family := range engineVersions {
			familySet[*family.DBParameterGroupFamily] = struct{}{}
		}
		var families []string
		for family := range familySet {
			families = append(families, family)
		}
		sort.Strings(families)
		familyIndex := scenario.questioner.AskChoice("Which family do you want to use?\n", families)
		log.Println("Creating a DB cluster parameter group.")
		_, err = scenario.dbClusters.CreateParameterGroup(
			parameterGroupName, families[familyIndex], "Example parameter group.")
		if err != nil {
			panic(err)
		}
		parameterGroup, err = scenario.dbClusters.GetParameterGroup(parameterGroupName)
		if err != nil {
			panic(err)
		}
	}
	log.Printf("Parameter group %v:\n", *parameterGroup.DBParameterGroupFamily)
	log.Printf("\tName: %v\n", *parameterGroup.DBClusterParameterGroupName)
	log.Printf("\tARN: %v\n", *parameterGroup.DBClusterParameterGroupArn)
	log.Printf("\tFamily: %v\n", *parameterGroup.DBParameterGroupFamily)
	log.Printf("\tDescription: %v\n", *parameterGroup.Description)
	log.Println(strings.Repeat("-", 88))
	return parameterGroup

}

// SetUserParameters shows how to get the parameters contained in a custom parameter
// group and update some of the parameter values in the group.
func (scenario GetStartedClusters) SetUserParameters(parameterGroupName string) {
	log.Println("Let's set some parameter values in your parameter group.")
	dbParameters, err := scenario.dbClusters.GetParameters(parameterGroupName, "")
	if err != nil {
		panic(err)
	}
	var updateParams []types.Parameter
	for _, dbParam := range dbParameters {
		if strings.HasPrefix(*dbParam.ParameterName, "auto_increment") &&
			dbParam.IsModifiable && *dbParam.DataType == "integer" {
			log.Printf("The %v parameter is described as:\n\t%v",
				*dbParam.ParameterName, *dbParam.Description)
			rangeSplit := strings.Split(*dbParam.AllowedValues, "-")
			lower, _ := strconv.Atoi(rangeSplit[0])
			upper, _ := strconv.Atoi(rangeSplit[1])
			newValue := scenario.questioner.AskInt(
				fmt.Sprintf("Enter a value between %v and %v:", lower, upper),
				demotools.InIntRange{Lower: lower, Upper: upper})
			dbParam.ParameterValue = aws.String(strconv.Itoa(newValue))
			updateParams = append(updateParams, dbParam)
		}
	}
	err = scenario.dbClusters.UpdateParameters(parameterGroupName, updateParams)
	if err != nil {
		panic(err)
	}
	log.Println("You can get a list of parameters you've set by specifying a source of 'user'.")
	userParameters, err := scenario.dbClusters.GetParameters(parameterGroupName, "user")
	if err != nil {
		panic(err)
	}
	log.Println("Here are the parameters you've set:")
	for _, param := range userParameters {
		log.Printf("\t%v: %v\n", *param.ParameterName, *param.ParameterValue)
	}
	log.Println(strings.Repeat("-", 88))
}

// CreateCluster shows how to create an Aurora DB cluster that contains a database
// of a specified type. The database is also configured to use a custom DB cluster
// parameter group.
func (scenario GetStartedClusters) CreateCluster(clusterName string, dbEngine string,
	dbName string, parameterGroup *types.DBClusterParameterGroup) *types.DBCluster {

	log.Println("Checking for an existing DB cluster.")
	cluster, err := scenario.dbClusters.GetDbCluster(clusterName)
	if err != nil {
		panic(err)
	}
	if cluster == nil {
		adminUsername := scenario.questioner.Ask(
			"Enter an administrator user name for the database: ", demotools.NotEmpty{})
		adminPassword := scenario.questioner.Ask(
			"Enter a password for the administrator (at least 8 characters): ", demotools.NotEmpty{})
		engineVersions, err := scenario.dbClusters.GetEngineVersions(dbEngine, *parameterGroup.DBParameterGroupFamily)
		if err != nil {
			panic(err)
		}
		var engineChoices []string
		for _, engine := range engineVersions {
			engineChoices = append(engineChoices, *engine.EngineVersion)
		}
		log.Println("The available engines for your parameter group are:")
		engineIndex := scenario.questioner.AskChoice("Which engine do you want to use?\n", engineChoices)
		log.Printf("Creating DB cluster %v and database %v.\n", clusterName, dbName)
		log.Printf("The DB cluster is configured to use\nyour custom parameter group %v\n",
			*parameterGroup.DBClusterParameterGroupName)
		log.Printf("and selected engine %v.\n", engineChoices[engineIndex])
		log.Println("This typically takes several minutes.")
		cluster, err = scenario.dbClusters.CreateDbCluster(
			clusterName, *parameterGroup.DBClusterParameterGroupName, dbName, dbEngine,
			engineChoices[engineIndex], adminUsername, adminPassword)
		if err != nil {
			panic(err)
		}
		for *cluster.Status != "available" {
			scenario.helper.Pause(30)
			cluster, err = scenario.dbClusters.GetDbCluster(clusterName)
			if err != nil {
				panic(err)
			}
			log.Println("Cluster created and available.")
		}
	}
	log.Println("Cluster data:")
	log.Printf("\tDBClusterIdentifier: %v\n", *cluster.DBClusterIdentifier)
	log.Printf("\tARN: %v\n", *cluster.DBClusterArn)
	log.Printf("\tStatus: %v\n", *cluster.Status)
	log.Printf("\tEngine: %v\n", *cluster.Engine)
	log.Printf("\tEngine version: %v\n", *cluster.EngineVersion)
	log.Printf("\tDBClusterParameterGroup: %v\n", *cluster.DBClusterParameterGroup)
	log.Printf("\tEngineMode: %v\n", *cluster.EngineMode)
	log.Println(strings.Repeat("-", 88))
	return cluster
}

// CreateInstance shows how to create a DB instance in an existing Aurora DB cluster.
// A new DB cluster contains no DB instances, so you must add one. The first DB instance
// that is added to a DB cluster defaults to a read-write DB instance.
func (scenario GetStartedClusters) CreateInstance(cluster *types.DBCluster) *types.DBInstance {
	log.Println("Checking for an existing database instance.")
	dbInstance, err := scenario.dbClusters.GetInstance(*cluster.DBClusterIdentifier)
	if err != nil {
		panic(err)
	}
	if dbInstance == nil {
		log.Println("Let's create a database instance in your DB cluster.")
		log.Println("First, choose a DB instance type:")
		instOpts, err := scenario.dbClusters.GetOrderableInstances(
			*cluster.Engine, *cluster.EngineVersion)
		if err != nil {
			panic(err)
		}
		var instChoices []string
		for _, opt := range instOpts {
			instChoices = append(instChoices, *opt.DBInstanceClass)
		}
		instIndex := scenario.questioner.AskChoice(
			"Which DB instance class do you want to use?\n", instChoices)
		log.Println("Creating a database instance. This typically takes several minutes.")
		dbInstance, err = scenario.dbClusters.CreateInstanceInCluster(
			*cluster.DBClusterIdentifier, *cluster.DBClusterIdentifier, *cluster.Engine,
			instChoices[instIndex])
		if err != nil {
			panic(err)
		}
		for *dbInstance.DBInstanceStatus != "available" {
			scenario.helper.Pause(30)
			dbInstance, err = scenario.dbClusters.GetInstance(*cluster.DBClusterIdentifier)
			if err != nil {
				panic(err)
			}
		}
	}
	log.Println("Instance data:")
	log.Printf("\tDBInstanceIdentifier: %v\n", *dbInstance.DBInstanceIdentifier)
	log.Printf("\tARN: %v\n", *dbInstance.DBInstanceArn)
	log.Printf("\tStatus: %v\n", *dbInstance.DBInstanceStatus)
	log.Printf("\tEngine: %v\n", *dbInstance.Engine)
	log.Printf("\tEngine version: %v\n", *dbInstance.EngineVersion)
	log.Println(strings.Repeat("-", 88))
	return dbInstance
}

// DisplayConnection displays connection information about an Aurora DB cluster and tips
// on how to connect to it.
func (scenario GetStartedClusters) DisplayConnection(cluster *types.DBCluster) {
	log.Println(
		"You can now connect to your database using your favorite MySql client.\n" +
			"One way to connect is by using the 'mysql' shell on an Amazon EC2 instance\n" +
			"that is running in the same VPC as your database cluster. Pass the endpoint,\n" +
			"port, and administrator user name to 'mysql' and enter your password\n" +
			"when prompted:")
	log.Printf("\n\tmysql -h %v -P %v -u %v -p\n",
		*cluster.Endpoint, *cluster.Port, *cluster.MasterUsername)
	log.Println("For more information, see the User Guide for Aurora:\n" +
		"\thttps://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_GettingStartedAurora.CreatingConnecting.Aurora.html#CHAP_GettingStartedAurora.Aurora.Connect")
	log.Println(strings.Repeat("-", 88))
}

// CreateSnapshot shows how to create a DB cluster snapshot and wait until it's available.
func (scenario GetStartedClusters) CreateSnapshot(clusterName string) {
	if scenario.questioner.AskBool(
		"Do you want to create a snapshot of your DB cluster (y/n)? ", "y") {
		snapshotId := fmt.Sprintf("%v-%v", clusterName, scenario.helper.UniqueId())
		log.Printf("Creating a snapshot named %v. This typically takes a few minutes.\n", snapshotId)
		snapshot, err := scenario.dbClusters.CreateClusterSnapshot(clusterName, snapshotId)
		if err != nil {
			panic(err)
		}
		for *snapshot.Status != "available" {
			scenario.helper.Pause(30)
			snapshot, err = scenario.dbClusters.GetClusterSnapshot(snapshotId)
			if err != nil {
				panic(err)
			}
		}
		log.Println("Snapshot data:")
		log.Printf("\tDBClusterSnapshotIdentifier: %v\n", *snapshot.DBClusterSnapshotIdentifier)
		log.Printf("\tARN: %v\n", *snapshot.DBClusterSnapshotArn)
		log.Printf("\tStatus: %v\n", *snapshot.Status)
		log.Printf("\tEngine: %v\n", *snapshot.Engine)
		log.Printf("\tEngine version: %v\n", *snapshot.EngineVersion)
		log.Printf("\tDBClusterIdentifier: %v\n", *snapshot.DBClusterIdentifier)
		log.Printf("\tSnapshotCreateTime: %v\n", *snapshot.SnapshotCreateTime)
		log.Println(strings.Repeat("-", 88))
	}
}

// Cleanup shows how to clean up a DB instance, DB cluster, and DB cluster parameter group.
// Before the DB cluster parameter group can be deleted, all associated DB instances and
// DB clusters must first be deleted.
func (scenario GetStartedClusters) Cleanup(dbInstance *types.DBInstance, cluster *types.DBCluster,
	parameterGroup *types.DBClusterParameterGroup) {

	if scenario.questioner.AskBool(
		"\nDo you want to delete the database instance, DB cluster, and parameter group (y/n)? ", "y") {
		log.Printf("Deleting database instance %v.\n", *dbInstance.DBInstanceIdentifier)
		err := scenario.dbClusters.DeleteInstance(*dbInstance.DBInstanceIdentifier)
		if err != nil {
			panic(err)
		}
		log.Printf("Deleting database cluster %v.\n", *cluster.DBClusterIdentifier)
		err = scenario.dbClusters.DeleteDbCluster(*cluster.DBClusterIdentifier)
		if err != nil {
			panic(err)
		}
		log.Println(
			"Waiting for the DB instance and DB cluster to delete. This typically takes several minutes.")
		for dbInstance != nil || cluster != nil {
			scenario.helper.Pause(30)
			if dbInstance != nil {
				dbInstance, err = scenario.dbClusters.GetInstance(*dbInstance.DBInstanceIdentifier)
				if err != nil {
					panic(err)
				}
			}
			if cluster != nil {
				cluster, err = scenario.dbClusters.GetDbCluster(*cluster.DBClusterIdentifier)
				if err != nil {
					panic(err)
				}
			}
		}
		log.Printf("Deleting parameter group %v.", *parameterGroup.DBClusterParameterGroupName)
		err = scenario.dbClusters.DeleteParameterGroup(*parameterGroup.DBClusterParameterGroupName)
		if err != nil {
			panic(err)
		}
	}
}

// snippet-end:[gov2.aurora.Scenario_GetStartedClusters]
