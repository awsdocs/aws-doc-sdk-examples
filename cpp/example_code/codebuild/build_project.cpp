// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/StartBuildRequest.h>
#include <aws/codebuild/model/StartBuildResult.h>
#include <iostream>

/**
 * Starts the project build based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: build_project <project_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String project_name(argv[1]);
    Aws::CodeBuild::CodeBuildClient codebuild;

    Aws::CodeBuild::Model::StartBuildRequest sb_req;
    sb_req.SetProjectName(project_name);

    auto sb_out = codebuild.StartBuild(sb_req);

    if (sb_out.IsSuccess())
    {
      std::cout << "Successfully started build" << std::endl;
    }

    else
    {
      std::cout << "Error starting build" << sb_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
