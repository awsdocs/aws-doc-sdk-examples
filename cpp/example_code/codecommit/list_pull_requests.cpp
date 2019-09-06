 
//snippet-sourcedescription:[list_pull_requests.cpp demonstrates how to list the pull requests for an AWS CodeCommit repository.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS CodeCommit]
//snippet-service:[codecommit]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
#include <aws/codecommit/model/ListPullRequestsRequest.h>
#include <aws/codecommit/model/ListPullRequestsResult.h>
#include <iostream>

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
  }

  Aws::ShutdownAPI(options);
  return 0;
}
