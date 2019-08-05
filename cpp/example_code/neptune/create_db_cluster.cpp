 
//snippet-sourcedescription:[create_db_cluster.cpp demonstrates how to create an Amazon Neptune DB cluster.]
//snippet-keyword:[C++]
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
#include <aws/neptune/model/CreateDBClusterRequest.h>
#include <aws/neptune/model/CreateDBClusterResult.h>
#include <iostream>

/**
 * Creates a Neptune db cluster based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_db_cluster <db_cluster_identifier>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_cluster_identifier(argv[1]);
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::CreateDBClusterRequest cdbc_req;
    cdbc_req.SetDBClusterIdentifier(db_cluster_identifier);
    cdbc_req.SetEngine("neptune");

    auto cdbc_out = neptune.CreateDBCluster(cdbc_req);

    if (cdbc_out.IsSuccess())
    {
      std::cout << "Successfully created neptune db cluster " << std::endl;
    }

    else
    {
      std::cout << "Error creating neptune db cluster " << cdbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
