// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/redshift/RedshiftClient.h>
#include <aws/redshift/model/CreateClusterSubnetGroupRequest.h>
#include <aws/redshift/model/CreateClusterSubnetGroupResult.h>
#include <aws/redshift/model/ClusterSubnetGroup.h>
#include <iostream>

/**
 * Create a subnet group for an Amazon Redshift cluster
 */
Aws::Redshift::Model::ClusterSubnetGroup * CreateClusterSubnetGroup(
    const Aws::String & subnetGroupName,
    const Aws::Vector<Aws::String> & subnetIds,
    const Aws::String & description,
    Aws::Redshift::Model::ClusterSubnetGroup & subnetGroup)
{
    Aws::Redshift::RedshiftClient redshift;
    Aws::Redshift::Model::CreateClusterSubnetGroupRequest redshift_req;

    redshift_req.SetClusterSubnetGroupName(subnetGroupName);
    redshift_req.SetSubnetIds(subnetIds);
    redshift_req.SetDescription(description);

    auto outcome = redshift.CreateClusterSubnetGroup(redshift_req);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error creating cluster subnet group. " <<
            outcome.GetError().GetMessage() << std::endl;
        return NULL;
    }

    auto result = outcome.GetResult();
    subnetGroup = result.GetClusterSubnetGroup();
    return &subnetGroup;
}

/**
 * Exercise CreateClusterSubnetGroup()
 */
int main(int argc, char **argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Set these configuration values before running the program
        Aws::String subnetGroupName = "myredshiftsubnetgroup";
        Aws::Vector<Aws::String> subnetIds;
        subnetIds.push_back("subnet-12345678");
        Aws::String description = "Redshift cluster subnet group";
        Aws::Redshift::Model::ClusterSubnetGroup subnetGroup;

        // Create a subnet group for the cluster
        if (!CreateClusterSubnetGroup(subnetGroupName, subnetIds, description,
            subnetGroup))
        {
            return 1;
        }

        // Print some information about the subnet group
        std::cout << "Created cluster subnet group: " << subnetGroup.GetClusterSubnetGroupName() << "\n";
        std::cout << "VPC ID: " << subnetGroup.GetVpcId() << "\n";
        std::cout << "Subnet group status: " << subnetGroup.GetSubnetGroupStatus() << std::endl;
        for (auto &subnet : subnetGroup.GetSubnets())
        {
            std::cout << "Subnet ID: " << subnet.GetSubnetIdentifier() << "\n";
            std::cout << "    Availability Zone: " << subnet.GetSubnetAvailabilityZone().GetName() << "\n";
            std::cout << "    Status: " << subnet.GetSubnetStatus() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
