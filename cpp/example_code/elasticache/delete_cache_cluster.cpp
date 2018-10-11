 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]



/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/elasticache/ElastiCacheClient.h>
#include <aws/elasticache/model/DeleteCacheClusterRequest.h>
#include <aws/elasticache/model/DeleteCacheClusterResult.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_cache_cluster <cluster_id>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String cluster_id(argv[1]);

    Aws::ElastiCache::ElastiCacheClient elasticache;

    Aws::ElastiCache::Model::DeleteCacheClusterRequest ccc_req;

    ccc_req.SetCacheClusterId(cluster_id);

    auto ccc_out = elasticache.DeleteCacheCluster(ccc_req);

    if (ccc_out.IsSuccess())
    {
      std::cout << "Successfully deleted cache cluster" << std::endl;
    }
    else
    {
      std::cout << "Error deleting cache cluster" << ccc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
