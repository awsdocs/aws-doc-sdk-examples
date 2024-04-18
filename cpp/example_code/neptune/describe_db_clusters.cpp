// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/neptune/NeptuneClient.h>
#include <aws/neptune/model/DescribeDBClustersRequest.h>
#include <aws/neptune/model/DescribeDBClustersResult.h>
#include <iostream>

/**
 * Describes Neptune db cluster based on command line input
 */

int main(int argc, char **argv) {
    if (argc != 1) {
        std::cout << "Usage: describe_db_cluster";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Neptune::NeptuneClient neptune;

        Aws::Neptune::Model::DescribeDBClustersRequest ddbc_req;

        Aws::String marker; // Used for pagination.
        Aws::Vector<Aws::Neptune::Model::DBCluster> all_clusters;

        do {
            if (!marker.empty()) {
                ddbc_req.SetMarker(marker);
            }

            auto ddbc_out = neptune.DescribeDBClusters(ddbc_req);

            if (ddbc_out.IsSuccess()) {
                auto &db_clusters = ddbc_out.GetResult().GetDBClusters();
                all_clusters.insert(all_clusters.end(), db_clusters.begin(),
                                    db_clusters.end());
                marker = ddbc_out.GetResult().GetMarker();
            }

            else {
                std::cerr << "Error describing neptune db clusters "
                          << ddbc_out.GetError().GetMessage()
                          << std::endl;
                break;
            }

        } while (!marker.empty());

        std::cout << all_clusters.size() << " Neptune db cluster(s) found."
                  << std::endl;
        for (auto cluster: all_clusters) {
            std::cout << "Neptune db cluster id: " << cluster.GetDBClusterIdentifier()
                      << std::endl;
            std::cout << "Neptune db cluster status: " << cluster.GetStatus()
                      << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
