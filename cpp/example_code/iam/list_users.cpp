/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates listing all Iam users.
 *
 */

//snippet-start:[iam.cpp.list_users.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/ListUsersRequest.h>
#include <iomanip>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.list_users.inc]

// snippet-start:[iam.cpp.list_users.code]
//! List all Iam users.
/*!
  \sa listUsers()
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::listUsers(const Aws::Client::ClientConfiguration &clientConfig) {
    const Aws::String DATE_FORMAT = "%Y-%m-%d";
    Aws::IAM::IAMClient iam(clientConfig);
    Aws::IAM::Model::ListUsersRequest request;

    bool done = false;
    bool header = false;
    while (!done)
    {
        auto outcome = iam.ListUsers(request);
        if (!outcome.IsSuccess())
        {
            std::cerr << "Failed to list iam users:" <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
        }

        if (!header)
        {
            std::cout << std::left << std::setw(32) << "Name" <<
                      std::setw(30) << "ID" << std::setw(64) << "Arn" <<
                      std::setw(20) << "CreateDate" << std::endl;
            header = true;
        }

        const auto &users = outcome.GetResult().GetUsers();
        for (const auto &user : users)
        {
            std::cout << std::left << std::setw(32) << user.GetUserName() <<
                      std::setw(30) << user.GetUserId() << std::setw(64) <<
                      user.GetArn() << std::setw(20) <<
                      user.GetCreateDate().ToGmtString(DATE_FORMAT.c_str()) << std::endl;
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

    return true;
}
// snippet-end:[iam.cpp.list_users.code]

/*
 *
 *  main function
 *
 * Usage: 'run_lists_users'
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
        AwsDoc::IAM::listUsers(clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD