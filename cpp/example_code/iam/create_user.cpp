/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates creating an IAM user.
 *
 */

//snippet-start:[iam.cpp.create_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateUserRequest.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>
#include <iostream>
#include "iam_samples.h"
//snippet-end:[iam.cpp.create_user.inc]

//! Creates an IAM user.
/*!
  \sa createUser()
  \param userName: The user name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/
bool AwsDoc::IAM::createUser(const Aws::String &userName,
                             const Aws::Client::ClientConfiguration &clientConfig) {
    // snippet-start:[iam.cpp.create_user01.code]
    Aws::IAM::IAMClient iam(clientConfig);
    // snippet-end:[iam.cpp.create_user01.code]
    // snippet-start:[iam.cpp.get_user.code]
    Aws::IAM::Model::GetUserRequest get_request;
    get_request.SetUserName(userName);

    auto get_outcome = iam.GetUser(get_request);
    if (get_outcome.IsSuccess()) {
        std::cout << "IAM user " << userName << " already exists" << std::endl;
        return true;
    }
    else if (get_outcome.GetError().GetErrorType() !=
             Aws::IAM::IAMErrors::NO_SUCH_ENTITY) {
        std::cerr << "Error checking existence of IAM user " << userName << ":"
                  << get_outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    // snippet-end:[iam.cpp.get_user.code]

    // snippet-start:[iam.cpp.create_user02.code]
    Aws::IAM::Model::CreateUserRequest create_request;
    create_request.SetUserName(userName);

    auto create_outcome = iam.CreateUser(create_request);
    if (!create_outcome.IsSuccess()) {
        std::cerr << "Error creating IAM user " << userName << ":" <<
                  create_outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully created IAM user " << userName << std::endl;
    }

    return create_outcome.IsSuccess();
    // snippet-end:[iam.cpp.create_user02.code]
}

/*
 *
 *  main function
 *
 * Usage: 'run_create_user <user_name>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_create_user <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String userName(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::IAM::createUser(userName, clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif  // TESTING_BUILD
