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
    Aws::String sort_order_type(argv[1]);
    Aws::CodeBuild::CodeBuildClient codebuild;

    Aws::CodeBuild::Model::ListBuildsRequest lb_req;
    Aws::CodeBuild::Model::BatchGetBuildsRequest bgb_req;
    lb_req.SetSortOrder(sort_order_type);

    auto lb_out = codebuild.ListBuilds(lb_req);

    if (lb_out.IsSuccess())
    {
      std::cout << "Information about each build:" << std::endl;
      for (auto val : lb_out.GetResult().GetIds())
      {
        bgb_req.SetIds(val);
        auto bgb_out = codebuild.BatchGetBuilds(bgb_req);

        if (bgb_out.IsSuccess())
        {
          for (auto val: bgb_out.GetResult().GetBuilds())
          {
            std::cout << val << std::endl;
          }
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
