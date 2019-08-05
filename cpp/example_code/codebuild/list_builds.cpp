 
//snippet-sourcedescription:[list_builds.cpp demonstrates how to retrieve and list build information using AWS CodeBuild.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeBuild]
//snippet-service:[codebuild]
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
#include <aws/core/utils/Outcome.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/ListBuildsRequest.h>
#include <aws/codebuild/model/ListBuildsResult.h>
#include <aws/codebuild/model/BatchGetBuildsRequest.h>
#include <aws/codebuild/model/BatchGetBuildsResult.h>
#include <iostream>

/**
 * Gets the list of builds and information about each build based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_builds <sort_order_type>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::CodeBuild::CodeBuildClient codebuild;

    Aws::CodeBuild::Model::ListBuildsRequest lb_req;
    Aws::CodeBuild::Model::BatchGetBuildsRequest bgb_req;

    if (Aws::Utils::StringUtils::CaselessCompare(argv[1], "ASCENDING"))
    {
      lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::ASCENDING);
    }
    else if(Aws::Utils::StringUtils::CaselessCompare(argv[1], "DESCENDING"))
    {
      lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::DESCENDING);
    }
    else
    {
      lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::NOT_SET);
    }

    auto lb_out = codebuild.ListBuilds(lb_req);

    if (lb_out.IsSuccess())
    {
      std::cout << "Information about each build:" << std::endl;
      bgb_req.SetIds(lb_out.GetResult().GetIds());
      auto bgb_out = codebuild.BatchGetBuilds(bgb_req);

      if (bgb_out.IsSuccess())
      {
        for (auto val: bgb_out.GetResult().GetBuilds())
        {
          std::cout << val.GetId() << std::endl;
        }
      }
    }

    else
    {
      std::cout << "Error listing builds" << lb_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
