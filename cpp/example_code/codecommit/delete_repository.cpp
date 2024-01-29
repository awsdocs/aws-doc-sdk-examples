// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
