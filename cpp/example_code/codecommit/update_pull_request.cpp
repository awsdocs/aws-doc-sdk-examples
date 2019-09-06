 
//snippet-sourcedescription:[update_pull_request.cpp demonstrates how to update an AWS CodeCommit pull request.]
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
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/UpdatePullRequestDescriptionRequest.h>
#include <aws/codecommit/model/UpdatePullRequestDescriptionResult.h>
#include <aws/codecommit/model/UpdatePullRequestStatusRequest.h>
#include <aws/codecommit/model/UpdatePullRequestStatusResult.h>
#include <aws/codecommit/model/UpdatePullRequestTitleRequest.h>
#include <aws/codecommit/model/UpdatePullRequestTitleRequest.h>
#include <aws/codecommit/model/Target.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

/**
 * Updates pull request based on command line input.
 */

int main(int argc, char ** argv)
{
  if (argc != 5)
  {
    std::cout << "Usage: update_pull_request <pull_request_id> <pull_request_description>"
                 "<pull_request_status> <pull_request_title>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String pull_request_id(argv[1]);
    Aws::String pull_request_description(argv[2]);
    Aws::String pull_request_status(argv[3]);
    Aws::String pull_request_title(argv[4]);

    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::UpdatePullRequestDescriptionRequest uprd_req;
    Aws::CodeCommit::Model::UpdatePullRequestStatusRequest uprs_req;
    Aws::CodeCommit::Model::UpdatePullRequestTitleRequest uprt_req;

    uprd_req.SetPullRequestId(pull_request_id);
    uprd_req.SetDescription(pull_request_description);

    uprs_req.SetPullRequestId(pull_request_id);
    if (pull_request_status == "OPEN")
    {
      uprs_req.SetPullRequestStatus(Aws::CodeCommit::Model::PullRequestStatusEnum::OPEN);
    }
    if (pull_request_status == "CLOSED")
    {
      uprs_req.SetPullRequestStatus(Aws::CodeCommit::Model::PullRequestStatusEnum::CLOSED);
    }
    else
    {
      uprs_req.SetPullRequestStatus(Aws::CodeCommit::Model::PullRequestStatusEnum::NOT_SET);
    }

    uprt_req.SetPullRequestId(pull_request_id);
    uprt_req.SetTitle(pull_request_title);

    auto uprd_out = codecommit.UpdatePullRequestDescription(uprd_req);
    auto uprs_out = codecommit.UpdatePullRequestStatus(uprs_req);
    auto uprt_out = codecommit.UpdatePullRequestTitle(uprt_req);

    if (uprd_out.IsSuccess() && uprs_out.IsSuccess() && uprt_out.IsSuccess())
    {
      std::cout << "Successfully updated pull request title, status and description."
                << std::endl;
    }
    else
    {
      std::cout << "Error updating pull request."
                << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
