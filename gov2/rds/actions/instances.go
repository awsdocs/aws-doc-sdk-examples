// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"errors"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/rds"
	"github.com/aws/aws-sdk-go-v2/service/rds/types"
)

// snippet-start:[gov2.rds.DbInstances.complete]
// snippet-start:[gov2.rds.DbInstances.struct]

type DbInstances struct {
	RdsClient *rds.Client
}

// snippet-end:[gov2.rds.DbInstances.struct]
// snippet-start:[gov2.rds.DescribeDBParameterGroups]

// GetParameterGroup gets a DB parameter group by name.
func (instances *DbInstances) GetParameterGroup(parameterGroupName string) (
	*types.DBParameterGroup, error) {
	output, err := instances.RdsClient.DescribeDBParameterGroups(
		context.TODO(), &rds.DescribeDBParameterGroupsInput{
			DBParameterGroupName: aws.String(parameterGroupName),
		})
	if err != nil {
		var notFoundError *types.DBParameterGroupNotFoundFault
		if errors.As(err, &notFoundError) {
			log.Printf("Parameter group %v does not exist.\n", parameterGroupName)
			err = nil
		} else {
			log.Printf("Error getting parameter group %v: %v\n", parameterGroupName, err)
		}
		return nil, err
	} else {
		return &output.DBParameterGroups[0], err
	}
}

// snippet-end:[gov2.rds.DescribeDBParameterGroups]

// snippet-start:[gov2.rds.CreateDBParameterGroup]

// CreateParameterGroup creates a DB parameter group that is based on the specified
// parameter group family.
func (instances *DbInstances) CreateParameterGroup(
	parameterGroupName string, parameterGroupFamily string, description string) (
	*types.DBParameterGroup, error) {

	output, err := instances.RdsClient.CreateDBParameterGroup(context.TODO(),
		&rds.CreateDBParameterGroupInput{
			DBParameterGroupName:   aws.String(parameterGroupName),
			DBParameterGroupFamily: aws.String(parameterGroupFamily),
			Description:            aws.String(description),
		})
	if err != nil {
		log.Printf("Couldn't create parameter group %v: %v\n", parameterGroupName, err)
		return nil, err
	} else {
		return output.DBParameterGroup, err
	}
}

// snippet-end:[gov2.rds.CreateDBParameterGroup]

// snippet-start:[gov2.rds.DeleteDBParameterGroup]

// DeleteParameterGroup deletes the named DB parameter group.
func (instances *DbInstances) DeleteParameterGroup(parameterGroupName string) error {
	_, err := instances.RdsClient.DeleteDBParameterGroup(context.TODO(),
		&rds.DeleteDBParameterGroupInput{
			DBParameterGroupName: aws.String(parameterGroupName),
		})
	if err != nil {
		log.Printf("Couldn't delete parameter group %v: %v\n", parameterGroupName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.rds.DeleteDBParameterGroup]

// snippet-start:[gov2.rds.DescribeDBParameters]

// GetParameters gets the parameters that are contained in a DB parameter group.
func (instances *DbInstances) GetParameters(parameterGroupName string, source string) (
	[]types.Parameter, error) {

	var output *rds.DescribeDBParametersOutput
	var params []types.Parameter
	var err error
	parameterPaginator := rds.NewDescribeDBParametersPaginator(instances.RdsClient,
		&rds.DescribeDBParametersInput{
			DBParameterGroupName: aws.String(parameterGroupName),
			Source:               aws.String(source),
		})
	for parameterPaginator.HasMorePages() {
		output, err = parameterPaginator.NextPage(context.TODO())
		if err != nil {
			log.Printf("Couldn't get parameters for %v: %v\n", parameterGroupName, err)
			break
		} else {
			params = append(params, output.Parameters...)
		}
	}
	return params, err
}

// snippet-end:[gov2.rds.DescribeDBParameters]

// snippet-start:[gov2.rds.ModifyDBParameterGroup]

// UpdateParameters updates parameters in a named DB parameter group.
func (instances *DbInstances) UpdateParameters(parameterGroupName string, params []types.Parameter) error {
	_, err := instances.RdsClient.ModifyDBParameterGroup(context.TODO(),
		&rds.ModifyDBParameterGroupInput{
			DBParameterGroupName: aws.String(parameterGroupName),
			Parameters:           params,
		})
	if err != nil {
		log.Printf("Couldn't update parameters in %v: %v\n", parameterGroupName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.rds.ModifyDBParameterGroup]

// snippet-start:[gov2.rds.CreateDBSnapshot]

// CreateSnapshot creates a snapshot of a DB instance.
func (instances *DbInstances) CreateSnapshot(instanceName string, snapshotName string) (
	*types.DBSnapshot, error) {
	output, err := instances.RdsClient.CreateDBSnapshot(context.TODO(), &rds.CreateDBSnapshotInput{
		DBInstanceIdentifier: aws.String(instanceName),
		DBSnapshotIdentifier: aws.String(snapshotName),
	})
	if err != nil {
		log.Printf("Couldn't create snapshot %v: %v\n", snapshotName, err)
		return nil, err
	} else {
		return output.DBSnapshot, nil
	}
}

// snippet-end:[gov2.rds.CreateDBSnapshot]

// snippet-start:[gov2.rds.DescribeDBSnapshots]

// GetSnapshot gets a DB instance snapshot.
func (instances *DbInstances) GetSnapshot(snapshotName string) (*types.DBSnapshot, error) {
	output, err := instances.RdsClient.DescribeDBSnapshots(context.TODO(),
		&rds.DescribeDBSnapshotsInput{
			DBSnapshotIdentifier: aws.String(snapshotName),
		})
	if err != nil {
		log.Printf("Couldn't get snapshot %v: %v\n", snapshotName, err)
		return nil, err
	} else {
		return &output.DBSnapshots[0], nil
	}
}

// snippet-end:[gov2.rds.DescribeDBSnapshots]

// snippet-start:[gov2.rds.CreateDBInstance]

// CreateInstance creates a DB instance.
func (instances *DbInstances) CreateInstance(instanceName string, dbName string,
	dbEngine string, dbEngineVersion string, parameterGroupName string, dbInstanceClass string,
	storageType string, allocatedStorage int32, adminName string, adminPassword string) (
	*types.DBInstance, error) {
	output, err := instances.RdsClient.CreateDBInstance(context.TODO(), &rds.CreateDBInstanceInput{
		DBInstanceIdentifier: aws.String(instanceName),
		DBName:               aws.String(dbName),
		DBParameterGroupName: aws.String(parameterGroupName),
		Engine:               aws.String(dbEngine),
		EngineVersion:        aws.String(dbEngineVersion),
		DBInstanceClass:      aws.String(dbInstanceClass),
		StorageType:          aws.String(storageType),
		AllocatedStorage:     aws.Int32(allocatedStorage),
		MasterUsername:       aws.String(adminName),
		MasterUserPassword:   aws.String(adminPassword),
	})
	if err != nil {
		log.Printf("Couldn't create instance %v: %v\n", instanceName, err)
		return nil, err
	} else {
		return output.DBInstance, nil
	}
}

// snippet-end:[gov2.rds.CreateDBInstance]

// snippet-start:[gov2.rds.DescribeDBInstances]

// GetInstance gets data about a DB instance.
func (instances *DbInstances) GetInstance(instanceName string) (
	*types.DBInstance, error) {
	output, err := instances.RdsClient.DescribeDBInstances(context.TODO(),
		&rds.DescribeDBInstancesInput{
			DBInstanceIdentifier: aws.String(instanceName),
		})
	if err != nil {
		var notFoundError *types.DBInstanceNotFoundFault
		if errors.As(err, &notFoundError) {
			log.Printf("DB instance %v does not exist.\n", instanceName)
			err = nil
		} else {
			log.Printf("Couldn't get instance %v: %v\n", instanceName, err)
		}
		return nil, err
	} else {
		return &output.DBInstances[0], nil
	}
}

// snippet-end:[gov2.rds.DescribeDBInstances]

// snippet-start:[gov2.rds.DeleteDBInstance]

// DeleteInstance deletes a DB instance.
func (instances *DbInstances) DeleteInstance(instanceName string) error {
	_, err := instances.RdsClient.DeleteDBInstance(context.TODO(), &rds.DeleteDBInstanceInput{
		DBInstanceIdentifier:   aws.String(instanceName),
		SkipFinalSnapshot:      true,
		DeleteAutomatedBackups: aws.Bool(true),
	})
	if err != nil {
		log.Printf("Couldn't delete instance %v: %v\n", instanceName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.rds.DeleteDBInstance]

// snippet-start:[gov2.rds.DescribeDBEngineVersions]

// GetEngineVersions gets database engine versions that are available for the specified engine
// and parameter group family.
func (instances *DbInstances) GetEngineVersions(engine string, parameterGroupFamily string) (
	[]types.DBEngineVersion, error) {
	output, err := instances.RdsClient.DescribeDBEngineVersions(context.TODO(),
		&rds.DescribeDBEngineVersionsInput{
			Engine:                 aws.String(engine),
			DBParameterGroupFamily: aws.String(parameterGroupFamily),
		})
	if err != nil {
		log.Printf("Couldn't get engine versions for %v: %v\n", engine, err)
		return nil, err
	} else {
		return output.DBEngineVersions, nil
	}
}

// snippet-end:[gov2.rds.DescribeDBEngineVersions]

// snippet-start:[gov2.rds.DescribeOrderableDBInstanceOptions]

// GetOrderableInstances uses a paginator to get DB instance options that can be used to create DB instances that are
// compatible with a set of specifications.
func (instances *DbInstances) GetOrderableInstances(engine string, engineVersion string) (
	[]types.OrderableDBInstanceOption, error) {

	var output *rds.DescribeOrderableDBInstanceOptionsOutput
	var instanceOptions []types.OrderableDBInstanceOption
	var err error
	orderablePaginator := rds.NewDescribeOrderableDBInstanceOptionsPaginator(instances.RdsClient,
		&rds.DescribeOrderableDBInstanceOptionsInput{
			Engine:        aws.String(engine),
			EngineVersion: aws.String(engineVersion),
		})
	for orderablePaginator.HasMorePages() {
		output, err = orderablePaginator.NextPage(context.TODO())
		if err != nil {
			log.Printf("Couldn't get orderable DB instance options: %v\n", err)
			break
		} else {
			instanceOptions = append(instanceOptions, output.OrderableDBInstanceOptions...)
		}
	}
	return instanceOptions, err
}

// snippet-end:[gov2.rds.DescribeOrderableDBInstanceOptions]
// snippet-end:[gov2.rds.DbInstances.complete]
