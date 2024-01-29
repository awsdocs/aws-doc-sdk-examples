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

int main(int argc, char **argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: describe_db_cluster";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::DescribeDBClustersRequest ddbc_req;

    auto ddbc_out = neptune.DescribeDBClusters(ddbc_req);

    if (ddbc_out.IsSuccess())
    {
      std::cout << "Successfully described db clusters request:";

      for (auto val: ddbc_out.GetResult().GetDBClusters())
      {
        std::cout << " " << val.GetDBClusterIdentifier() << std::endl;
      }
    }

    else
    {
      std::cout << "Error describing neptune db clusters " << ddbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
