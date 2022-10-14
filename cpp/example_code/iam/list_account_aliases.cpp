/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment,
 * including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html.
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates listing all account aliases associated with an AWS account.
 *
 */

//snippet-start:[iam.cpp.list_account_aliases.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/ListAccountAliasesRequest.h>
#include <aws/iam/model/ListAccountAliasesResult.h>
#include <iomanip>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.list_account_aliases.inc]

//! Lists all account aliases associated with an AWS account.
/*!
  \sa listAccountAliases()
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/
// snippet-start:[iam.cpp.list_account_aliases.code]
bool
AwsDoc::IAM::listAccountAliases(const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::ListAccountAliasesRequest request;

    bool done = false;
    bool header = false;
    while (!done) {
        auto outcome = iam.ListAccountAliases(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Failed to list account aliases: " <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
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

        for (const auto &alias: aliases) {
            std::cout << std::left << std::setw(32) << alias << std::endl;
        }

        if (outcome.GetResult().GetIsTruncated()) {
            request.SetMarker(outcome.GetResult().GetMarker());
        }
        else {
            done = true;
        }
    }

    return true;
}
// snippet-end:[iam.cpp.list_account_aliases.code]

/*
 *
 *  main function
 *
 * Usage: 'Usage: 'run_list_account_aliases'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::listAccountAliases(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
