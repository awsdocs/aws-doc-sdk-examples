// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

// snippet-start:[gov2.redshift.Imports]

import (
	"context"
	"errors"
	"log"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/redshift"
	"github.com/aws/aws-sdk-go-v2/service/redshift/types"
)

// snippet-end:[gov2.redshift.Imports]

// snippet-start:[gov2.redshift.ActionsStruct]

// RedshiftActions wraps Redshift service actions.
type RedshiftActions struct {
	RedshiftClient *redshift.Client
}

// snippet-end:[gov2.redshift.ActionsStruct]

// snippet-start:[gov2.redshift.CreateCluster]

// CreateCluster sends a request to create a cluster with the given clusterId using the provided credentials.
func (actor RedshiftActions) CreateCluster(ctx context.Context, clusterId string, userName string, userPassword string, nodeType string, clusterType string, publiclyAccessible bool) (*redshift.CreateClusterOutput, error) {
	// Create a new Redshift cluster
	input := &redshift.CreateClusterInput{
		ClusterIdentifier:  aws.String(clusterId),
		MasterUserPassword: aws.String(userPassword),
		MasterUsername:     aws.String(userName),
		NodeType:           aws.String(nodeType),
		ClusterType:        aws.String(clusterType),
		PubliclyAccessible: aws.Bool(publiclyAccessible),
	}
	var opErr *types.ClusterAlreadyExistsFault
	output, err := actor.RedshiftClient.CreateCluster(ctx, input)
	if err != nil && errors.As(err, &opErr) {
		log.Println("Cluster already exists")
		return nil, nil
	} else if err != nil {
		log.Printf("Failed to create Redshift cluster: %v\n", err)
		return nil, err
	}

	log.Printf("Created cluster %s\n", *output.Cluster.ClusterIdentifier)
	return output, nil
}

// snippet-end:[gov2.redshift.CreateCluster]

// snippet-start:[gov2.redshift.ModifyCluster]

// ModifyCluster sets the preferred maintenance window for the given cluster.
func (actor RedshiftActions) ModifyCluster(ctx context.Context, clusterId string, maintenanceWindow string) *redshift.ModifyClusterOutput {
	// Modify the cluster's maintenance window
	input := &redshift.ModifyClusterInput{
		ClusterIdentifier:          aws.String(clusterId),
		PreferredMaintenanceWindow: aws.String(maintenanceWindow),
	}

	var opErr *types.InvalidClusterStateFault
	output, err := actor.RedshiftClient.ModifyCluster(ctx, input)
	if err != nil && errors.As(err, &opErr) {
		log.Println("Cluster is in an invalid state.")
		panic(err)
	} else if err != nil {
		log.Printf("Failed to modify Redshift cluster: %v\n", err)
		panic(err)
	}

	log.Printf("The cluster was successfully modified and now has %s as the maintenance window\n", *output.Cluster.PreferredMaintenanceWindow)
	return output
}

// snippet-end:[gov2.redshift.ModifyCluster]

// snippet-start:[gov2.redshift.DeleteCluster]

// DeleteCluster deletes the given cluster.
func (actor RedshiftActions) DeleteCluster(ctx context.Context, clusterId string) (bool, error) {
	input := redshift.DeleteClusterInput{
		ClusterIdentifier:        aws.String(clusterId),
		SkipFinalClusterSnapshot: aws.Bool(true),
	}
	_, err := actor.RedshiftClient.DeleteCluster(ctx, &input)
	var opErr *types.ClusterNotFoundFault
	if err != nil && errors.As(err, &opErr) {
		log.Println("Cluster was not found. Where could it be?")
		return false, err
	} else if err != nil {
		log.Printf("Failed to delete Redshift cluster: %v\n", err)
		return false, err
	}
	waiter := redshift.NewClusterDeletedWaiter(actor.RedshiftClient)
	err = waiter.Wait(ctx, &redshift.DescribeClustersInput{
		ClusterIdentifier: aws.String(clusterId),
	}, 5*time.Minute)
	if err != nil {
		log.Printf("Wait time exceeded for deleting cluster, continuing: %v\n", err)
	}
	log.Printf("The cluster %s was deleted\n", clusterId)
	return true, nil
}

// snippet-end:[gov2.redshift.DeleteCluster]

// snippet-start:[gov2.redshift.DescribeClusters]

// DescribeClusters returns information about the given cluster.
func (actor RedshiftActions) DescribeClusters(ctx context.Context, clusterId string) (*redshift.DescribeClustersOutput, error) {
	input, err := actor.RedshiftClient.DescribeClusters(ctx, &redshift.DescribeClustersInput{
		ClusterIdentifier: aws.String(clusterId),
	})
	var opErr *types.AccessToClusterDeniedFault
	if errors.As(err, &opErr) {
		println("Access to cluster denied.")
		panic(err)
	} else if err != nil {
		println("Failed to describe Redshift clusters.")
		return nil, err
	}
	return input, nil
}

// snippet-end:[gov2.redshift.DescribeClusters]
