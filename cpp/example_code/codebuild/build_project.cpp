// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
build_project.cpp demonstrates how to start building a project with AWS CodeBuild.

*/
//snippet-start:[cb.cpp.build_project.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/StartBuildRequest.h>
#include <aws/codebuild/model/StartBuildResult.h>
#include <iostream>
//snippet-end:[cb.cpp.build_project.inc]
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
    //snippet-start:[cb.cpp.build_project]
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
        //snippet-end:[cb.cpp.build_project]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
