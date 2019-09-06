 
//snippet-sourcedescription:[describe_db_clusers.cpp demonstrates how to retrieve information about Amazon Neptune provisioned DB clusters.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Neptune]
//snippet-service:[neptune]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
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
