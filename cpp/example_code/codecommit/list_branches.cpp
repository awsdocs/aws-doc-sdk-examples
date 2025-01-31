// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/ListBranchesRequest.h>
#include <aws/codecommit/model/ListBranchesResult.h>
#include <iostream>

/**
 * Lists branches of a repository based on command line inputs
 */

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: list_branches <repository_name>"
                  << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String repository_name(argv[1]);

        Aws::CodeCommit::CodeCommitClient codecommit;

        Aws::CodeCommit::Model::ListBranchesRequest lb_req;

        lb_req.SetRepositoryName(repository_name);

        Aws::Vector<Aws::String> all_branches;
        Aws::String next_token; // Used for pagination.

        do {
            if (!next_token.empty()) {
                lb_req.SetNextToken(next_token);
            }

            // List branches.
            auto lb_out = codecommit.ListBranches(lb_req);

            if (lb_out.IsSuccess()) {
                const auto &branches = lb_out.GetResult().GetBranches();
                all_branches.insert(all_branches.cend(), branches.cbegin(),
                                    branches.cbegin());

                next_token = lb_out.GetResult().GetNextToken();
            }
            else {
                std::cout << "Error listing branches." << lb_out.GetError().GetMessage()
                          << std::endl;
                break;
            }
        } while (!next_token.empty());

        std::cout << all_branches.size() << " branch(es) found." << std::endl;

        for (const auto &branch: all_branches) {
            std::cout << "  " << branch << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
