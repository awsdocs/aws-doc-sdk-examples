 
//snippet-sourcedescription:[create_cache_cluster.cpp demonstrates how to create an Amazon ElastiCache cluster.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon ElastiCache]
//snippet-service:[elasticache]
//snippet-sourcetype:[full-example]
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
#include <aws/elasticache/model/CreateCacheClusterRequest.h>
#include <aws/elasticache/model/CreateCacheClusterResult.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 5)
  {
    std::cout << "Usage: create_cache_cluster <cluster_id> <engine> <cache_node_type>"
                 "<num_cache_nodes>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String cluster_id(argv[1]);
    Aws::String engine(argv[2]);
    Aws::String cache_node_type(argv[3]);
    int num_cache_nodes = Aws::Utils::StringUtils::ConvertToInt64(argv[4]);

    Aws::ElastiCache::ElastiCacheClient elasticache;

    Aws::ElastiCache::Model::CreateCacheClusterRequest ccc_req;

    ccc_req.SetCacheClusterId(cluster_id);
    ccc_req.SetEngine(engine);
    ccc_req.SetCacheNodeType(cache_node_type);
    ccc_req.SetNumCacheNodes(num_cache_nodes);

    auto ccc_out = elasticache.CreateCacheCluster(ccc_req);

    if (ccc_out.IsSuccess())
    {
      std::cout << "Successfully created cache cluster" << std::endl;
    }
    else
    {
      std::cout << "Error creating cache cluster" << ccc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
