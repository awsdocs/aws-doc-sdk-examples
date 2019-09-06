 
//snippet-sourcedescription:[modify_cache_cluster.cpp demonstrates how to modify an Amazon ElastiCache cluster.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon ElastiCache]
//snippet-service:[elasticache]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]



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
