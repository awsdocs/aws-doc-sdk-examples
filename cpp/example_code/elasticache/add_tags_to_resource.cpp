// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
add_tags_to_resource.cpp demonstrates how to add a cost allocation tag to an Amazon ElastiCache resource.
*/
//snippet-start:[ecache.cpp.attach_volume.inc]
#include <aws/core/Aws.h>
#include <aws/elasticache/ElastiCacheClient.h>
#include <aws/elasticache/model/AddTagsToResourceRequest.h>
#include <aws/elasticache/model/AddTagsToResourceResult.h>
#include <aws/elasticache/model/Tag.h>
#include <iostream>
//snippet-end:[ecache.cpp.attach_volume.inc]

int main(int argc, char ** argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: add_tags <resource_name> <tag_key> <tag_value>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String name(argv[1]);
    Aws::String tag_key(argv[2]);
    Aws::String tag_value(argv[3]);

    //snippet-start:[ecache.cpp.attach_volume]
    Aws::ElastiCache::ElastiCacheClient elasticache;

    Aws::ElastiCache::Model::AddTagsToResourceRequest attr_req;
    Aws::ElastiCache::Model::Tag tags;

    tags.SetKey(tag_key);
    tags.SetValue(tag_value);

    attr_req.SetResourceName(name);
    attr_req.AddTags(tags);

    auto attr_out = elasticache.AddTagsToResource(attr_req);

    if (attr_out.IsSuccess())
    {
      std::cout << "Successfully added tags to resource" << std::endl;
    }
    else
    {
      std::cout << "Error adding tags" << attr_out.GetError().GetMessage()
        << std::endl;
    }
     //snippet-end:[ecache.cpp.attach_volume]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
