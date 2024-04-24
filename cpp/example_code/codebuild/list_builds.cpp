// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/codebuild/CodeBuildClient.h>
#include <aws/codebuild/model/ListBuildsRequest.h>
#include <aws/codebuild/model/ListBuildsResult.h>
#include <aws/codebuild/model/BatchGetBuildsRequest.h>
#include <aws/codebuild/model/BatchGetBuildsResult.h>
#include <iostream>

/**
 * Gets the list of builds and information about each build based on command line input
 */

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: list_builds <sort_order_type>";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::CodeBuild::CodeBuildClient codebuild;

        Aws::CodeBuild::Model::ListBuildsRequest lb_req;
        Aws::CodeBuild::Model::BatchGetBuildsRequest bgb_req;

        if (Aws::Utils::StringUtils::CaselessCompare(argv[1], "ASCENDING")) {
            lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::ASCENDING);
        }
        else if (Aws::Utils::StringUtils::CaselessCompare(argv[1], "DESCENDING")) {
            lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::DESCENDING);
        }
        else {
            lb_req.SetSortOrder(Aws::CodeBuild::Model::SortOrderType::NOT_SET);
        }

        Aws::String next_token; // Used for pagination.

        do {
            if (!next_token.empty()) {
                lb_req.SetNextToken(next_token);
            }

            auto lb_out = codebuild.ListBuilds(lb_req);

            if (lb_out.IsSuccess()) {
                std::cout << "Information about each build:" << std::endl;
                bgb_req.SetIds(lb_out.GetResult().GetIds());
                auto bgb_out = codebuild.BatchGetBuilds(bgb_req);

                if (bgb_out.IsSuccess()) {
                    const auto &builds = bgb_out.GetResult().GetBuilds();
                    std::cout << builds.size() << " build(s) found." << std::endl;
                    for (auto val: builds) {
                        std::cout << val.GetId() << std::endl;
                    }
                }

                next_token = lb_out.GetResult().GetNextToken();
            }

            else {
                std::cout << "Error listing builds" << lb_out.GetError().GetMessage()
                          << std::endl;
                break;
            }

        } while (!next_token.empty());

    }


    Aws::ShutdownAPI(options);
    return 0;
}
