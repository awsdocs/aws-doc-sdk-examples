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
