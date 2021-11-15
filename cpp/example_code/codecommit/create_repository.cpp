// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_repository.cpp demonstrates how to create an AWS CodeCommit repository.
*/
//snippet-start:[cc.cpp.create_repository.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/CreateRepositoryRequest.h>
#include <aws/codecommit/model/CreateRepositoryResult.h>
#include <aws/codecommit/model/RepositoryMetadata.h>
#include <iostream>
//snippet-end:[cc.cpp.create_repository.inc]

/**
 * Creates a repository based on command line inputs
 */

int main(int argc, char ** argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: create_repository <repository_name> <repository_description>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_name(argv[1]);
    Aws::String repository_description(argv[2]);

    //snippet-start:[cc.cpp.create_repository]
    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::CreateRepositoryRequest cr_req;

    cr_req.SetRepositoryName(repository_name);
    cr_req.SetRepositoryDescription(repository_description);

    auto cr_out = codecommit.CreateRepository(cr_req);

    if (cr_out.IsSuccess())
    {
      std::cout << "Successfully created repository with repository id:"
                << cr_out.GetResult().GetRepositoryMetadata().GetRepositoryId() << std::endl;
    }
    else
    {
      std::cout << "Error creating repository" << cr_out.GetError().GetMessage()
                << std::endl;
    }
    //snippet-end:[cc.cpp.create_repository]

  }

  Aws::ShutdownAPI(options);
  return 0;
}
