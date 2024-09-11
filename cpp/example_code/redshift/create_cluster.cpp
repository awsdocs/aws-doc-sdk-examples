// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/redshift/RedshiftClient.h>
#include <aws/redshift/model/CreateClusterRequest.h>
#include <aws/redshift/model/CreateClusterResult.h>
#include <iostream>


/**
 * Create an Amazon Redshift cluster
 */
Aws::Redshift::Model::Cluster * CreateRedshiftCluster(
    const Aws::String & dbName,
    const Aws::String & clusterId,
    const Aws::String & userName,
    const Aws::String & userPassword,
    const Aws::String & subnetGroupName,
    const Aws::String & nodeType,
    const int numberOfNodes,
    const Aws::Vector <Aws::String> & iamRoles,
    Aws::Redshift::Model::Cluster & cluster,
    const int automatedSnapshotPeriod=0)	// 0 = disable automated snapshots
{
    Aws::Redshift::RedshiftClient redshift;
    Aws::Redshift::Model::CreateClusterRequest redshift_req;

    // Initialize all request parameters
    redshift_req.SetDBName(dbName);
    redshift_req.SetClusterIdentifier(clusterId);
    redshift_req.SetMasterUsername(userName);
    redshift_req.SetMasterUserPassword(userPassword);
    redshift_req.SetClusterSubnetGroupName(subnetGroupName);
    redshift_req.SetNodeType(nodeType);
    redshift_req.SetNumberOfNodes(numberOfNodes);
    redshift_req.SetIamRoles(iamRoles);
    redshift_req.SetAutomatedSnapshotRetentionPeriod(automatedSnapshotPeriod);

    // Create the cluster. Does not wait for creation to complete.
    auto outcome = redshift.CreateCluster(redshift_req);

    if (!outcome.IsSuccess())
    {
        std::cerr << "Error creating cluster. " <<
            outcome.GetError().GetMessage() << std::endl;
        return NULL;
    }

    auto result = outcome.GetResult();
    cluster = result.GetCluster();
    return &cluster;
}

/**
 * Exercise CreateRedshiftCluster()
 */
int main(int argc, char **argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Set these configuration values before running the program
        Aws::String dbName = "dev";
        Aws::String clusterId = "redshift-cluster-1";
        Aws::String userName = "awsuser";
        Aws::String userPassword = "AWSuser_01";
        Aws::String subnetGroupName = "myredshiftsubnetgroup";
        Aws::String nodeType = "dc2.large";
        int numberOfNodes = 2;
        Aws::Vector <Aws::String> iamRoles;
        iamRoles.push_back("arn:aws:iam::ACCOUNT_ID:role/ROLE_NAME");
        Aws::Redshift::Model::Cluster cluster;

        // Create the cluster. Function returns before the cluster is fully built.
        if (!CreateRedshiftCluster(dbName, clusterId,
            userName, userPassword,
            subnetGroupName,
            nodeType, numberOfNodes,
            iamRoles,
            cluster))
        {
            return 1;
        }

        // Print some information about the cluster
        std::cout << "Creating cluster with ID " << cluster.GetClusterIdentifier() << "\n";
        std::cout << "Cluster database name: " << cluster.GetDBName() << "\n";
        std::cout << "Cluster status: " << cluster.GetClusterStatus() << std::endl;
    }
    Aws::ShutdownAPI(options);
    return 0;
}
