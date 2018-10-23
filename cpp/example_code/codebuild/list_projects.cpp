 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CodeBuild]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


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
#include <aws/core/utils/Outcome.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/ListProjectsRequest.h>
#include <aws/codebuild/model/ListProjectsResult.h>
#include <iostream>

/**
 * Gets the list of projects based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_projects <sort_order_type>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String sort_order_type(argv[1]);
    Aws::CodeBuild::CodeBuildClient codebuild;

    Aws::CodeBuild::Model::ListProjectsRequest lp_req;

    if (sort_order_type == "ASCENDING")
    {
      lp_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::ASCENDING);
    }
    else if (sort_order_type == "DESCENDING")
    {
      lp_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::DESCENDING);
    }
    else
    {
      lp_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::NOT_SET);
    }

    auto lp_out = codebuild.ListProjects(lp_req);

    if (lp_out.IsSuccess())
    {
      std::cout << "Successfully listing projects";
      for (auto val : lp_out.GetResult().GetProjects())
      {
        std::cout << " " << val << std::endl;
      }
    }

    else
    {
      std::cout << "Error listing projects" << lp_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
