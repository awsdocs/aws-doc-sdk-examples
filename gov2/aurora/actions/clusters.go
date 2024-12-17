// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package actions

// snippet-start:[gov2.aurora.DbClusters.complete]
// snippet-start:[gov2.aurora.DbClusters.struct]

import (
	"context"
	"errors"
	"log"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/rds"
	"github.com/aws/aws-sdk-go-v2/service/rds/types"
)

type DbClusters struct {
	AuroraClient *rds.Client
}

// snippet-end:[gov2.aurora.DbClusters.struct]
// snippet-start:[gov2.aurora.DescribeDBClusterParameterGroups]

// GetParameterGroup gets a DB cluster parameter group by name.
func (clusters *DbClusters) GetParameterGroup(ctx context.Context, parameterGroupName string) (
	*types.DBClusterParameterGroup, error) {
	output, err := clusters.AuroraClient.DescribeDBClusterParameterGroups(
		ctx, &rds.DescribeDBClusterParameterGroupsInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
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
		return &output.DBClusterParameterGroups[0], err
	}
}

// snippet-end:[gov2.aurora.DescribeDBClusterParameterGroups]
// snippet-start:[gov2.aurora.CreateDBClusterParameterGroup]

// CreateParameterGroup creates a DB cluster parameter group that is based on the specified
// parameter group family.
func (clusters *DbClusters) CreateParameterGroup(
	ctx context.Context, parameterGroupName string, parameterGroupFamily string, description string) (
	*types.DBClusterParameterGroup, error) {

	output, err := clusters.AuroraClient.CreateDBClusterParameterGroup(ctx,
		&rds.CreateDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			DBParameterGroupFamily:      aws.String(parameterGroupFamily),
			Description:                 aws.String(description),
		})
	if err != nil {
		log.Printf("Couldn't create parameter group %v: %v\n", parameterGroupName, err)
		return nil, err
	} else {
		return output.DBClusterParameterGroup, err
	}
}

// snippet-end:[gov2.aurora.CreateDBClusterParameterGroup]

// snippet-start:[gov2.aurora.DeleteDBClusterParameterGroup]

// DeleteParameterGroup deletes the named DB cluster parameter group.
func (clusters *DbClusters) DeleteParameterGroup(ctx context.Context, parameterGroupName string) error {
	_, err := clusters.AuroraClient.DeleteDBClusterParameterGroup(ctx,
		&rds.DeleteDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
		})
	if err != nil {
		log.Printf("Couldn't delete parameter group %v: %v\n", parameterGroupName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.aurora.DeleteDBClusterParameterGroup]

// snippet-start:[gov2.aurora.DescribeDBClusterParameters]

// GetParameters gets the parameters that are contained in a DB cluster parameter group.
func (clusters *DbClusters) GetParameters(ctx context.Context, parameterGroupName string, source string) (
	[]types.Parameter, error) {

	var output *rds.DescribeDBClusterParametersOutput
	var params []types.Parameter
	var err error
	parameterPaginator := rds.NewDescribeDBClusterParametersPaginator(clusters.AuroraClient,
		&rds.DescribeDBClusterParametersInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			Source:                      aws.String(source),
		})
	for parameterPaginator.HasMorePages() {
		output, err = parameterPaginator.NextPage(ctx)
		if err != nil {
			log.Printf("Couldn't get paramaeters for %v: %v\n", parameterGroupName, err)
			break
		} else {
			params = append(params, output.Parameters...)
		}
	}
	return params, err
}

// snippet-end:[gov2.aurora.DescribeDBClusterParameters]

// snippet-start:[gov2.aurora.ModifyDBClusterParameterGroup]

// UpdateParameters updates parameters in a named DB cluster parameter group.
func (clusters *DbClusters) UpdateParameters(ctx context.Context, parameterGroupName string, params []types.Parameter) error {
	_, err := clusters.AuroraClient.ModifyDBClusterParameterGroup(ctx,
		&rds.ModifyDBClusterParameterGroupInput{
			DBClusterParameterGroupName: aws.String(parameterGroupName),
			Parameters:                  params,
		})
	if err != nil {
		log.Printf("Couldn't update parameters in %v: %v\n", parameterGroupName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.aurora.ModifyDBClusterParameterGroup]

// snippet-start:[gov2.aurora.DescribeDBClusters]

// GetDbCluster gets data about an Aurora DB cluster.
func (clusters *DbClusters) GetDbCluster(ctx context.Context, clusterName string) (*types.DBCluster, error) {
	output, err := clusters.AuroraClient.DescribeDBClusters(ctx,
		&rds.DescribeDBClustersInput{
			DBClusterIdentifier: aws.String(clusterName),
		})
	if err != nil {
		var notFoundError *types.DBClusterNotFoundFault
		if errors.As(err, &notFoundError) {
			log.Printf("DB cluster %v does not exist.\n", clusterName)
			err = nil
		} else {
			log.Printf("Couldn't get DB cluster %v: %v\n", clusterName, err)
		}
		return nil, err
	} else {
		return &output.DBClusters[0], err
	}
}

// snippet-end:[gov2.aurora.DescribeDBClusters]

// snippet-start:[gov2.aurora.CreateDBCluster]

// CreateDbCluster creates a DB cluster that is configured to use the specified parameter group.
// The newly created DB cluster contains a database that uses the specified engine and
// engine version.
func (clusters *DbClusters) CreateDbCluster(ctx context.Context, clusterName string, parameterGroupName string,
	dbName string, dbEngine string, dbEngineVersion string, adminName string, adminPassword string) (
	*types.DBCluster, error) {

	output, err := clusters.AuroraClient.CreateDBCluster(ctx, &rds.CreateDBClusterInput{
		DBClusterIdentifier:         aws.String(clusterName),
		Engine:                      aws.String(dbEngine),
		DBClusterParameterGroupName: aws.String(parameterGroupName),
		DatabaseName:                aws.String(dbName),
		EngineVersion:               aws.String(dbEngineVersion),
		MasterUserPassword:          aws.String(adminPassword),
		MasterUsername:              aws.String(adminName),
	})
	if err != nil {
		log.Printf("Couldn't create DB cluster %v: %v\n", clusterName, err)
		return nil, err
	} else {
		return output.DBCluster, err
	}
}

// snippet-end:[gov2.aurora.CreateDBCluster]

// snippet-start:[gov2.aurora.DeleteDBCluster]

// DeleteDbCluster deletes a DB cluster without keeping a final snapshot.
func (clusters *DbClusters) DeleteDbCluster(ctx context.Context, clusterName string) error {
	_, err := clusters.AuroraClient.DeleteDBCluster(ctx, &rds.DeleteDBClusterInput{
		DBClusterIdentifier: aws.String(clusterName),
		SkipFinalSnapshot:   aws.Bool(true),
	})
	if err != nil {
		log.Printf("Couldn't delete DB cluster %v: %v\n", clusterName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.aurora.DeleteDBCluster]

// snippet-start:[gov2.aurora.CreateDBClusterSnapshot]

// CreateClusterSnapshot creates a snapshot of a DB cluster.
func (clusters *DbClusters) CreateClusterSnapshot(ctx context.Context, clusterName string, snapshotName string) (
	*types.DBClusterSnapshot, error) {
	output, err := clusters.AuroraClient.CreateDBClusterSnapshot(ctx, &rds.CreateDBClusterSnapshotInput{
		DBClusterIdentifier:         aws.String(clusterName),
		DBClusterSnapshotIdentifier: aws.String(snapshotName),
	})
	if err != nil {
		log.Printf("Couldn't create snapshot %v: %v\n", snapshotName, err)
		return nil, err
	} else {
		return output.DBClusterSnapshot, nil
	}
}

// snippet-end:[gov2.aurora.CreateDBClusterSnapshot]

// snippet-start:[gov2.aurora.DescribeDBClusterSnapshots]

// GetClusterSnapshot gets a DB cluster snapshot.
func (clusters *DbClusters) GetClusterSnapshot(ctx context.Context, snapshotName string) (*types.DBClusterSnapshot, error) {
	output, err := clusters.AuroraClient.DescribeDBClusterSnapshots(ctx,
		&rds.DescribeDBClusterSnapshotsInput{
			DBClusterSnapshotIdentifier: aws.String(snapshotName),
		})
	if err != nil {
		log.Printf("Couldn't get snapshot %v: %v\n", snapshotName, err)
		return nil, err
	} else {
		return &output.DBClusterSnapshots[0], nil
	}
}

// snippet-end:[gov2.aurora.DescribeDBClusterSnapshots]

// snippet-start:[gov2.aurora.CreateDBInstance]

// CreateInstanceInCluster creates a database instance in an existing DB cluster. The first database that is
// created defaults to a read-write DB instance.
func (clusters *DbClusters) CreateInstanceInCluster(ctx context.Context, clusterName string, instanceName string,
	dbEngine string, dbInstanceClass string) (*types.DBInstance, error) {
	output, err := clusters.AuroraClient.CreateDBInstance(ctx, &rds.CreateDBInstanceInput{
		DBInstanceIdentifier: aws.String(instanceName),
		DBClusterIdentifier:  aws.String(clusterName),
		Engine:               aws.String(dbEngine),
		DBInstanceClass:      aws.String(dbInstanceClass),
	})
	if err != nil {
		log.Printf("Couldn't create instance %v: %v\n", instanceName, err)
		return nil, err
	} else {
		return output.DBInstance, nil
	}
}

// snippet-end:[gov2.aurora.CreateDBInstance]

// snippet-start:[gov2.aurora.DescribeDBInstances]

// GetInstance gets data about a DB instance.
func (clusters *DbClusters) GetInstance(ctx context.Context, instanceName string) (
	*types.DBInstance, error) {
	output, err := clusters.AuroraClient.DescribeDBInstances(ctx,
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

// snippet-end:[gov2.aurora.DescribeDBInstances]

// snippet-start:[gov2.aurora.DeleteDBInstance]

// DeleteInstance deletes a DB instance.
func (clusters *DbClusters) DeleteInstance(ctx context.Context, instanceName string) error {
	_, err := clusters.AuroraClient.DeleteDBInstance(ctx, &rds.DeleteDBInstanceInput{
		DBInstanceIdentifier:   aws.String(instanceName),
		SkipFinalSnapshot:      aws.Bool(true),
		DeleteAutomatedBackups: aws.Bool(true),
	})
	if err != nil {
		log.Printf("Couldn't delete instance %v: %v\n", instanceName, err)
		return err
	} else {
		return nil
	}
}

// snippet-end:[gov2.aurora.DeleteDBInstance]

// snippet-start:[gov2.aurora.DescribeDBEngineVersions]

// GetEngineVersions gets database engine versions that are available for the specified engine
// and parameter group family.
func (clusters *DbClusters) GetEngineVersions(ctx context.Context, engine string, parameterGroupFamily string) (
	[]types.DBEngineVersion, error) {
	output, err := clusters.AuroraClient.DescribeDBEngineVersions(ctx,
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

// snippet-end:[gov2.aurora.DescribeDBEngineVersions]

// snippet-start:[gov2.aurora.DescribeOrderableDBInstanceOptions]

// GetOrderableInstances uses a paginator to get DB instance options that can be used to create DB instances that are
// compatible with a set of specifications.
func (clusters *DbClusters) GetOrderableInstances(ctx context.Context, engine string, engineVersion string) (
	[]types.OrderableDBInstanceOption, error) {

	var output *rds.DescribeOrderableDBInstanceOptionsOutput
	var instances []types.OrderableDBInstanceOption
	var err error
	orderablePaginator := rds.NewDescribeOrderableDBInstanceOptionsPaginator(clusters.AuroraClient,
		&rds.DescribeOrderableDBInstanceOptionsInput{
			Engine:        aws.String(engine),
			EngineVersion: aws.String(engineVersion),
		})
	for orderablePaginator.HasMorePages() {
		output, err = orderablePaginator.NextPage(ctx)
		if err != nil {
			log.Printf("Couldn't get orderable DB instances: %v\n", err)
			break
		} else {
			instances = append(instances, output.OrderableDBInstanceOptions...)
		}
	}
	return instances, err
}

// snippet-end:[gov2.aurora.DescribeOrderableDBInstanceOptions]
// snippet-end:[gov2.aurora.DbClusters.complete]
