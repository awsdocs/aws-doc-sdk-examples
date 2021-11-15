// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
list_pull_requests.cpp demonstrates how to list the pull requests for an AWS CodeCommit repository.

*/
//snippet-start:[cc.cpp.list_pull_requests.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/ListPullRequestsRequest.h>
#include <aws/codecommit/model/ListPullRequestsResult.h>
#include <iostream>
//snippet-end:[cc.cpp.list_pull_requests.inc]

/**
 * Lists pull requests of a repository based on command line inputs
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_pull_requests <repository_name>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_name(argv[1]);

    //snippet-start:[cc.cpp.list_pull_requests]
    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::ListPullRequestsRequest lpr_req;

    lpr_req.SetRepositoryName(repository_name);

    auto lpr_out = codecommit.ListPullRequests(lpr_req);

    if (lpr_out.IsSuccess())
    {
      std::cout << "Successfully listed pull requests with pull request ids as: ";

      for (auto val: lpr_out.GetResult().GetPullRequestIds())
      {
        std::cout << " " << val;
      }
    }
    else
    {
      std::cout << "Error getting pull requests" << lpr_out.GetError().GetMessage()
                << std::endl;
    }
        //snippet-end:[cc.cpp.list_pull_requests]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
