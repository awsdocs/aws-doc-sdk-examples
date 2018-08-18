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
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/DeleteRepositoryRequest.h>
#include <aws/codecommit/model/DeleteRepositoryResult.h>
#include <iostream>

/**
 * Deletes a repository based on command line inputs
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_repository <repository_name>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_name(argv[1]);

    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::DeleteRepositoryRequest dr_req;

    dr_req.SetRepositoryName(repository_name);

    auto dr_out = codecommit.DeleteRepository(dr_req);

    if (dr_out.IsSuccess())
    {
      std::cout << "Successfully deleted repository with repository id:"
                << dr_out.GetResult().GetRepositoryId()
                << std::endl;
    }
    else
    {
      std::cout << "Error deleting repository" << dr_out.GetError().GetMessage()
                << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
