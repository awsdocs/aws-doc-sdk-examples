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
 * Demonstrates updating an IAM user's name.
 *
 */

//snippet-start:[iam.cpp.update_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateUserRequest.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.update_user.inc]


//! Updates an IAM user's name.
/*!
  \sa updateUser()
  \param currentUserName: The current user's name.
  \param newUserName: The new user's name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

// snippet-start:[iam.cpp.update_user.code]
bool AwsDoc::IAM::updateUser(const Aws::String &currentUserName,
                const Aws::String &newUserName,
                const Aws::Client::ClientConfiguration &clientConfig)
{
    Aws::IAM::IAMClient iam(clientConfig);

    Aws::IAM::Model::UpdateUserRequest request;
    request.SetUserName(currentUserName);
    request.SetNewUserName(newUserName);

    auto outcome = iam.UpdateUser(request);
    if (outcome.IsSuccess())
    {
        std::cout << "IAM user " << currentUserName <<
                  " successfully updated with new user name " << newUserName <<
                  std::endl;
    }
    else
    {
        std::cerr << "Error updating user name for IAM user " << currentUserName <<
                  ":" << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[iam.cpp.update_user.code]

/*
 *
 *  main function
 *
 * Prerequisites: Existing IAM user's name.
 *
 * Usage: 'run_update_user <old_user_name> <new_user_name>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: run_update_user <old_user_name> <new_user_name>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String currentUserName(argv[1]);
        Aws::String newUserName(argv[2]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::updateUser(currentUserName, newUserName, clientConfig);

    }
    Aws::ShutdownAPI(options);
    return 0;
}
#endif  // TESTING_BUILD
