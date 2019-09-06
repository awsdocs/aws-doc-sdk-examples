 
//snippet-sourcedescription:[delete_db_cluster.cpp demonstrates how to delete an Amazon Neptune DB cluster.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
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
#include <aws/neptune/model/DeleteDBClusterRequest.h>
#include <aws/neptune/model/DeleteDBClusterResult.h>
#include <iostream>

/**
 * Deletes a Neptune db cluster based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_db_cluster <db_cluster_identifier>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_cluster_identifier(argv[1]);
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::DeleteDBClusterRequest ddbc_req;
    ddbc_req.SetDBClusterIdentifier(db_cluster_identifier);

    auto ddbc_out = neptune.DeleteDBCluster(ddbc_req);

    if (ddbc_out.IsSuccess())
    {
      std::cout << "Successfully deleted neptune db cluster " << std::endl;
    }

    else
    {
      std::cout << "Error deleting neptune db cluster " << ddbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
