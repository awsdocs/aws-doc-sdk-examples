// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
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
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/rds/actions"
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

// snippet-start:[gov2.rds.Scenario_GetStartedInstances]

// GetStartedInstances is an interactive example that shows you how to use the AWS SDK for Go
// with Amazon Relation Database Service (Amazon RDS) to do the following:
//
//  1. Create a custom DB parameter group and set parameter values.
//  2. Create a DB instance that is configured to use the parameter group. The DB instance
//     also contains a database.
//  3. Take a snapshot of the DB instance.
//  4. Delete the DB instance and parameter group.
type GetStartedInstances struct {
	sdkConfig  aws.Config
	instances  actions.DbInstances
	questioner demotools.IQuestioner
	helper     IScenarioHelper
	isTestRun  bool
}

// NewGetStartedInstances constructs a GetStartedInstances instance from a configuration.
// It uses the specified config to get an Amazon RDS
// client and create wrappers for the actions used in the scenario.
func NewGetStartedInstances(sdkConfig aws.Config, questioner demotools.IQuestioner,
	helper IScenarioHelper) GetStartedInstances {
	rdsClient := rds.NewFromConfig(sdkConfig)
	return GetStartedInstances{
		sdkConfig:  sdkConfig,
		instances:  actions.DbInstances{RdsClient: rdsClient},
		questioner: questioner,
		helper:     helper,
	}
}

// Run runs the interactive scenario.
func (scenario GetStartedInstances) Run(dbEngine string, parameterGroupName string,
	instanceName string, dbName string) {
	defer func() {
		if r := recover(); r != nil {
			log.Println("Something went wrong with the demo.")
		}
	}()

	log.Println(strings.Repeat("-", 88))
	log.Println("Welcome to the Amazon Relational Database Service (Amazon RDS) DB Instance demo.")
	log.Println(strings.Repeat("-", 88))

	parameterGroup := scenario.CreateParameterGroup(dbEngine, parameterGroupName)
	scenario.SetUserParameters(parameterGroupName)
	instance := scenario.CreateInstance(instanceName, dbEngine, dbName, parameterGroup)
	scenario.DisplayConnection(instance)
	scenario.CreateSnapshot(instance)
	scenario.Cleanup(instance, parameterGroup)

	log.Println(strings.Repeat("-", 88))
	log.Println("Thanks for watching!")
	log.Println(strings.Repeat("-", 88))
}

// CreateParameterGroup shows how to get available engine versions for a specified
// database engine and create a DB parameter group that is compatible with a
// selected engine family.
func (scenario GetStartedInstances) CreateParameterGroup(dbEngine string,
	parameterGroupName string) *types.DBParameterGroup {

	log.Printf("Checking for an existing DB parameter group named %v.\n",
		parameterGroupName)
	parameterGroup, err := scenario.instances.GetParameterGroup(parameterGroupName)
	if err != nil {
		panic(err)
	}
	if parameterGroup == nil {
		log.Printf("Getting available database engine versions for %v.\n", dbEngine)
		engineVersions, err := scenario.instances.GetEngineVersions(dbEngine, "")
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
		log.Println("Creating a DB parameter group.")
		_, err = scenario.instances.CreateParameterGroup(
			parameterGroupName, families[familyIndex], "Example parameter group.")
		if err != nil {
			panic(err)
		}
		parameterGroup, err = scenario.instances.GetParameterGroup(parameterGroupName)
		if err != nil {
			panic(err)
		}
	}
	log.Printf("Parameter group %v:\n", *parameterGroup.DBParameterGroupFamily)
	log.Printf("\tName: %v\n", *parameterGroup.DBParameterGroupName)
	log.Printf("\tARN: %v\n", *parameterGroup.DBParameterGroupArn)
	log.Printf("\tFamily: %v\n", *parameterGroup.DBParameterGroupFamily)
	log.Printf("\tDescription: %v\n", *parameterGroup.Description)
	log.Println(strings.Repeat("-", 88))
	return parameterGroup
}

// SetUserParameters shows how to get the parameters contained in a custom parameter
// group and update some of the parameter values in the group.
func (scenario GetStartedInstances) SetUserParameters(parameterGroupName string) {
	log.Println("Let's set some parameter values in your parameter group.")
	dbParameters, err := scenario.instances.GetParameters(parameterGroupName, "")
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
	err = scenario.instances.UpdateParameters(parameterGroupName, updateParams)
	if err != nil {
		panic(err)
	}
	log.Println("To get a list of parameters that you set previously, specify a source of 'user'.")
	userParameters, err := scenario.instances.GetParameters(parameterGroupName, "user")
	if err != nil {
		panic(err)
	}
	log.Println("Here are the parameters you set:")
	for _, param := range userParameters {
		log.Printf("\t%v: %v\n", *param.ParameterName, *param.ParameterValue)
	}
	log.Println(strings.Repeat("-", 88))
}

// CreateInstance shows how to create a DB instance that contains a database of a
// specified type. The database is also configured to use a custom DB parameter group.
func (scenario GetStartedInstances) CreateInstance(instanceName string, dbEngine string,
	dbName string, parameterGroup *types.DBParameterGroup) *types.DBInstance {

	log.Println("Checking for an existing DB instance.")
	instance, err := scenario.instances.GetInstance(instanceName)
	if err != nil {
		panic(err)
	}
	if instance == nil {
		adminUsername := scenario.questioner.Ask(
			"Enter an administrator username for the database: ", demotools.NotEmpty{})
		adminPassword := scenario.questioner.AskPassword(
			"Enter a password for the administrator (at least 8 characters): ", 7)
		engineVersions, err := scenario.instances.GetEngineVersions(dbEngine,
			*parameterGroup.DBParameterGroupFamily)
		if err != nil {
			panic(err)
		}
		var engineChoices []string
		for _, engine := range engineVersions {
			engineChoices = append(engineChoices, *engine.EngineVersion)
		}
		engineIndex := scenario.questioner.AskChoice(
			"The available engines for your parameter group are:\n", engineChoices)
		engineSelection := engineVersions[engineIndex]
		instOpts, err := scenario.instances.GetOrderableInstances(*engineSelection.Engine,
			*engineSelection.EngineVersion)
		if err != nil {
			panic(err)
		}
		optSet := map[string]struct{}{}
		for _, opt := range instOpts {
			if strings.Contains(*opt.DBInstanceClass, "micro") {
				optSet[*opt.DBInstanceClass] = struct{}{}
			}
		}
		var optChoices []string
		for opt := range optSet {
			optChoices = append(optChoices, opt)
		}
		sort.Strings(optChoices)
		optIndex := scenario.questioner.AskChoice(
			"The available micro DB instance classes for your database engine are:\n", optChoices)
		storageType := "standard"
		allocatedStorage := int32(5)
		log.Printf("Creating a DB instance named %v and database %v.\n"+
			"The DB instance is configured to use your custom parameter group %v,\n"+
			"selected engine %v,\n"+
			"selected DB instance class %v,"+
			"and %v GiB of %v storage.\n"+
			"This typically takes several minutes.",
			instanceName, dbName, *parameterGroup.DBParameterGroupName, *engineSelection.EngineVersion,
			optChoices[optIndex], allocatedStorage, storageType)
		instance, err = scenario.instances.CreateInstance(
			instanceName, dbName, *engineSelection.Engine, *engineSelection.EngineVersion,
			*parameterGroup.DBParameterGroupName, optChoices[optIndex], storageType,
			allocatedStorage, adminUsername, adminPassword)
		if err != nil {
			panic(err)
		}
		for *instance.DBInstanceStatus != "available" {
			scenario.helper.Pause(30)
			instance, err = scenario.instances.GetInstance(instanceName)
			if err != nil {
				panic(err)
			}
		}
		log.Println("Instance created and available.")
	}
	log.Println("Instance data:")
	log.Printf("\tDBInstanceIdentifier: %v\n", *instance.DBInstanceIdentifier)
	log.Printf("\tARN: %v\n", *instance.DBInstanceArn)
	log.Printf("\tStatus: %v\n", *instance.DBInstanceStatus)
	log.Printf("\tEngine: %v\n", *instance.Engine)
	log.Printf("\tEngine version: %v\n", *instance.EngineVersion)
	log.Println(strings.Repeat("-", 88))
	return instance
}

// DisplayConnection displays connection information about a DB instance and tips
// on how to connect to it.
func (scenario GetStartedInstances) DisplayConnection(instance *types.DBInstance) {
	log.Println(
		"You can now connect to your database by using your favorite MySQL client.\n" +
			"One way to connect is by using the 'mysql' shell on an Amazon EC2 instance\n" +
			"that is running in the same VPC as your DB instance. Pass the endpoint,\n" +
			"port, and administrator username to 'mysql'. Then, enter your password\n" +
			"when prompted:")
	log.Printf("\n\tmysql -h %v -P %v -u %v -p\n",
		*instance.Endpoint.Address, instance.Endpoint.Port, *instance.MasterUsername)
	log.Println("For more information, see the User Guide for RDS:\n" +
		"\thttps://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_GettingStarted.CreatingConnecting.MySQL.html#CHAP_GettingStarted.Connecting.MySQL")
	log.Println(strings.Repeat("-", 88))
}

// CreateSnapshot shows how to create a DB instance snapshot and wait until it's available.
func (scenario GetStartedInstances) CreateSnapshot(instance *types.DBInstance) {
	if scenario.questioner.AskBool(
		"Do you want to create a snapshot of your DB instance (y/n)? ", "y") {
		snapshotId := fmt.Sprintf("%v-%v", *instance.DBInstanceIdentifier, scenario.helper.UniqueId())
		log.Printf("Creating a snapshot named %v. This typically takes a few minutes.\n", snapshotId)
		snapshot, err := scenario.instances.CreateSnapshot(*instance.DBInstanceIdentifier, snapshotId)
		if err != nil {
			panic(err)
		}
		for *snapshot.Status != "available" {
			scenario.helper.Pause(30)
			snapshot, err = scenario.instances.GetSnapshot(snapshotId)
			if err != nil {
				panic(err)
			}
		}
		log.Println("Snapshot data:")
		log.Printf("\tDBSnapshotIdentifier: %v\n", *snapshot.DBSnapshotIdentifier)
		log.Printf("\tARN: %v\n", *snapshot.DBSnapshotArn)
		log.Printf("\tStatus: %v\n", *snapshot.Status)
		log.Printf("\tEngine: %v\n", *snapshot.Engine)
		log.Printf("\tEngine version: %v\n", *snapshot.EngineVersion)
		log.Printf("\tDBInstanceIdentifier: %v\n", *snapshot.DBInstanceIdentifier)
		log.Printf("\tSnapshotCreateTime: %v\n", *snapshot.SnapshotCreateTime)
		log.Println(strings.Repeat("-", 88))
	}
}

// Cleanup shows how to clean up a DB instance and DB parameter group.
// Before the DB parameter group can be deleted, all associated DB instances must first be deleted.
func (scenario GetStartedInstances) Cleanup(
	instance *types.DBInstance, parameterGroup *types.DBParameterGroup) {

	if scenario.questioner.AskBool(
		"\nDo you want to delete the database instance and parameter group (y/n)? ", "y") {
		log.Printf("Deleting database instance %v.\n", *instance.DBInstanceIdentifier)
		err := scenario.instances.DeleteInstance(*instance.DBInstanceIdentifier)
		if err != nil {
			panic(err)
		}
		log.Println(
			"Waiting for the DB instance to delete. This typically takes several minutes.")
		for instance != nil {
			scenario.helper.Pause(30)
			instance, err = scenario.instances.GetInstance(*instance.DBInstanceIdentifier)
			if err != nil {
				panic(err)
			}
		}
		log.Printf("Deleting parameter group %v.", *parameterGroup.DBParameterGroupName)
		err = scenario.instances.DeleteParameterGroup(*parameterGroup.DBParameterGroupName)
		if err != nil {
			panic(err)
		}
	}
}

// snippet-end:[gov2.rds.Scenario_GetStartedInstances]
