// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/ListPullRequestsRequest.h>
#include <aws/codecommit/model/ListPullRequestsResult.h>
#include <iostream>

/**
 * Lists pull requests of a repository based on command line inputs
 */

int main(int argc, char **argv) {
    if (argc != 2) {
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

        Aws::Vector<Aws::String> all_pull_request;
        Aws::String next_token; // Used for pagination.

        do {
            if (!next_token.empty()) {
                lpr_req.SetNextToken(next_token);
            }
            auto lpr_out = codecommit.ListPullRequests(lpr_req);

            if (lpr_out.IsSuccess()) {
                const auto &lpr_res = lpr_out.GetResult().GetPullRequestIds();
                all_pull_request.insert(all_pull_request.cend(), lpr_res.cbegin(),
                                        lpr_res.cend());

                next_token = lpr_out.GetResult().GetNextToken();
            }
            else {
                std::cout << "Error getting pull requests"
                          << lpr_out.GetError().GetMessage()
                          << std::endl;
                break;
            }
        } while (!next_token.empty());

        std::cout << all_pull_request.size() << " pull request(s) found." << std::endl;

        for (const auto &pull_request: all_pull_request) {
            std::cout << "Pull request: " << pull_request << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
