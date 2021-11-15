// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
update_repository.cpp demonstrates how to update information about an AWS CodeCommit repository.
*/
//snippet-start:[cc.cpp.update_repository.inc]
#include <aws/core/Aws.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/UpdateRepositoryDescriptionRequest.h>
#include <aws/codecommit/model/UpdateRepositoryNameRequest.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>
//snippet-end:[cc.cpp.update_repository.inc]


/**
 * Updates repository based on command line input.
 */

int main(int argc, char ** argv)
{
  if (argc != 5)
  {
    std::cout << "Usage: update_repository <repository_old_name> <repository_new_name>"
                 "<repository_description>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_old_name(argv[1]);
    Aws::String repository_new_name(argv[2]);
    Aws::String repository_description(argv[3]);

    //snippet-start:[cc.cpp.update_repository]
    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::UpdateRepositoryDescriptionRequest urd_req;
    Aws::CodeCommit::Model::UpdateRepositoryNameRequest urn_req;

    urd_req.SetRepositoryName(repository_old_name);
    urd_req.SetRepositoryDescription(repository_description);

    urn_req.SetOldName(repository_old_name);
    urn_req.SetNewName(repository_new_name);

    auto urd_out = codecommit.UpdateRepositoryDescription(urd_req);
    auto urn_out = codecommit.UpdateRepositoryName(urn_req);

    if (urd_out.IsSuccess() && urn_out.IsSuccess())
    {
      std::cout << "Successfully updated repository."
                << std::endl;
    }
    else
    {
      std::cout << "Error updating repository."
                << std::endl;
    }
        //snippet-end:[cc.cpp.update_repository]

  }

  Aws::ShutdownAPI(options);
  return 0;
}
