// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_cache_cluster.cpp demonstrates how to delete an Amazon ElastiCache cluster.
*/
//snippet-start:[ecache.cpp.delete_cache_cluster.inc]
#include <aws/core/Aws.h>
#include <aws/elasticache/ElastiCacheClient.h>
#include <aws/elasticache/model/DeleteCacheClusterRequest.h>
#include <aws/elasticache/model/DeleteCacheClusterResult.h>
#include <iostream>
//snippet-end:[ecache.cpp.delete_cache_cluster.inc]

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

    //snippet-start:[ecache.cpp.delete_cache_cluster]
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
        //snippet-end:[ecache.cpp.delete_cache_cluster]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
