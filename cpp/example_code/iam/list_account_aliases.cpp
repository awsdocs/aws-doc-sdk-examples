// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
list_account_aliases.cpp demonstrates how to retrieve information about the aliases for an AWS account.
*/

//snippet-start:[iam.cpp.list_account_aliases.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/ListAccountAliasesRequest.h>
#include <aws/iam/model/ListAccountAliasesResult.h>
#include <iomanip>
#include <iostream>
//snippet-end:[iam.cpp.list_account_aliases.inc]

/**
 * Lists all account aliases associated with an AWS account
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[iam.cpp.list_account_aliases.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::ListAccountAliasesRequest request;

        bool done = false;
        bool header = false;
        while (!done)
        {
            auto outcome = iam.ListAccountAliases(request);
            if (!outcome.IsSuccess())
            {
                std::cout << "Failed to list account aliases: " <<
                    outcome.GetError().GetMessage() << std::endl;
                break;
            }

            const auto &aliases = outcome.GetResult().GetAccountAliases();
            if (!header)
            {
                if (aliases.size() == 0)
                {
                    std::cout << "Account has no aliases" << std::endl;
                    break;
                }
                std::cout << std::left << std::setw(32) << "Alias" << std::endl;
                header = true;
            }

            for (const auto &alias : aliases)
            {
                std::cout << std::left << std::setw(32) << alias << std::endl;
            }

            if (outcome.GetResult().GetIsTruncated())
            {
                request.SetMarker(outcome.GetResult().GetMarker());
            }
            else
            {
                done = true;
            }
        }
        // snippet-end:[iam.cpp.list_account_aliases.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

