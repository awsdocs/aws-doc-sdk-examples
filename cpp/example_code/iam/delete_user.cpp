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
 * Demonstrates deleting an IAM user.
 * This api only works for users with noassociated resources, for example groups and policies.
 * To delete a user in the non-trivial case, use the deleteUser operation within the
 * aws-cpp-sdk-access-management high level sdk.
 *
 */

//snippet-start:[iam.cpp.delete_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeleteUserRequest.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.delete_user.inc]

//! Deletes an IAM user.
/*!
  \sa deleteUser()
  \param userName: The user name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/

bool AwsDoc::IAM::deleteUser(const Aws::String &userName,
                             const Aws::Client::ClientConfiguration &clientConfig) {
    // snippet-start:[iam.cpp.delete_user01.code]
    Aws::IAM::IAMClient iam(clientConfig);
    // snippet-end:[iam.cpp.delete_user01.code]
    Aws::IAM::Model::GetUserRequest get_request;
    get_request.SetUserName(userName);

    auto get_outcome = iam.GetUser(get_request);
    if (!get_outcome.IsSuccess()) {
        if (get_outcome.GetError().GetErrorType() ==
            Aws::IAM::IAMErrors::NO_SUCH_ENTITY) {
            std::cout << "IAM user " << userName << " does not exist" <<
                      std::endl;
        }
        else {
            std::cerr << "Error checking existence of IAM user " << userName <<
                      ": " << get_outcome.GetError().GetMessage() << std::endl;
        }
        return false;
    }

    // snippet-start:[iam.cpp.delete_user02.code]
    Aws::IAM::Model::DeleteUserRequest request;
    request.SetUserName(userName);
    auto outcome = iam.DeleteUser(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting IAM user " << userName << ": " <<
                  outcome.GetError().GetMessage() << std::endl;;
    }
    else {
        std::cout << "Successfully deleted IAM user " << userName << std::endl;
    }

    return outcome.IsSuccess();
    // snippet-end:[iam.cpp.delete_user02.code]
}

/*
 *
 *  main function
 *
 * Prerequisites: Existing IAM user name.
 *
 * Usage: 'run_delete_user <user_name>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_delete_user <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String user_name(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::deleteUser(user_name, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD

