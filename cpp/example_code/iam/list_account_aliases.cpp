/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/ListAccountAliasesRequest.h>
#include <aws/iam/model/ListAccountAliasesResult.h>
#include <iomanip>
#include <iostream>

/**
 * Lists all account aliases associated with an AWS account
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::ListAccountAliasesRequest request;

        bool done = false;
        bool header = false;
        while (!done) {
            auto outcome = iam.ListAccountAliases(request);
            if (!outcome.IsSuccess()) {
                std::cout << "Failed to list account aliases: " <<
                    outcome.GetError().GetMessage() << std::endl;
                break;
            }

            const auto &aliases = outcome.GetResult().GetAccountAliases();
            if (!header) {
                if (aliases.size() == 0) {
                    std::cout << "Account has no aliases" << std::endl;
                    break;
                }
                std::cout << std::left << std::setw(32) << "Alias" << std::endl;
                header = true;
            }

            for (const auto &alias : aliases) {
                std::cout << std::left << std::setw(32) << alias << std::endl;
            }

            if (outcome.GetResult().GetIsTruncated()) {
                request.SetMarker(outcome.GetResult().GetMarker());
            } else {
                done = true;
            }
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

