// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/elasticache/ElastiCacheClient.h>
#include <aws/elasticache/model/ModifyCacheClusterRequest.h>
#include <aws/elasticache/model/ModifyCacheClusterResult.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: modify_cache_cluster <cluster_id> <topic_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String cluster_id(argv[1]);
    Aws::String topic_arn(argv[2]);

    Aws::ElastiCache::ElastiCacheClient elasticache;

    Aws::ElastiCache::Model::ModifyCacheClusterRequest mcc_req;

    mcc_req.SetCacheClusterId(cluster_id);
    mcc_req.SetNotificationTopicArn(topic_arn);

    auto mcc_out = elasticache.ModifyCacheCluster(mcc_req);

    if (mcc_out.IsSuccess())
    {
      std::cout << "Successfully modified cache cluster" << std::endl;
    }
    else
    {
      std::cout << "Error creating cache cluster" << mcc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
