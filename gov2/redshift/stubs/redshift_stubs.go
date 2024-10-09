// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package stubs

import (
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/redshift"
	"github.com/aws/aws-sdk-go-v2/service/redshift/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func StubDescribeClusters(clusterId string, raiseErr *testtools.StubError) testtools.Stub {
	clusters := []types.Cluster{
		{
			ClusterStatus:     aws.String("available"),
			ClusterCreateTime: aws.Time(time.Now()),
		},
	}
	return testtools.Stub{
		OperationName: "DescribeClusters",
		Input: &redshift.DescribeClustersInput{
			ClusterIdentifier: &clusterId,
		},
		Output: &redshift.DescribeClustersOutput{
			Clusters: clusters,
		},
		Error: raiseErr,
	}
}

func StubCreateCluster(clusterId string, userName string, userPassword string, nodeType string, clusterType string, publiclyAccessible bool, raiseErr *testtools.StubError) testtools.Stub {
	input := &redshift.CreateClusterInput{
		ClusterIdentifier:  aws.String(clusterId),
		MasterUserPassword: aws.String(userPassword),
		MasterUsername:     aws.String(userName),
		NodeType:           aws.String(nodeType),
		ClusterType:        aws.String(clusterType),
		PubliclyAccessible: aws.Bool(publiclyAccessible),
	}
	return testtools.Stub{
		OperationName: "CreateCluster",
		Input:         input,
		Output: &redshift.CreateClusterOutput{
			Cluster: &types.Cluster{
				ClusterStatus:     aws.String("available"),
				ClusterIdentifier: aws.String("test-cluster-identifier"),
			},
		},
		Error: raiseErr,
	}
}

func StubModifyCluster(raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "ModifyCluster",
		Input: &redshift.ModifyClusterInput{
			ClusterIdentifier:          aws.String("demo-cluster-1"),
			PreferredMaintenanceWindow: aws.String("wed:07:30-wed:08:00"),
		},
		Output: &redshift.ModifyClusterOutput{
			Cluster: &types.Cluster{
				PreferredMaintenanceWindow: aws.String("wed:07:30-wed:08:00"),
			},
		},
		Error: raiseErr,
	}
}

func StubDeleteCluster(clusterId string, raiseErr *testtools.StubError) testtools.Stub {
	return testtools.Stub{
		OperationName: "DeleteCluster",
		Input:         &redshift.DeleteClusterInput{ClusterIdentifier: aws.String(clusterId), SkipFinalClusterSnapshot: aws.Bool(true)},
		Output:        &redshift.DeleteClusterOutput{},
		Error:         raiseErr,
	}
}
