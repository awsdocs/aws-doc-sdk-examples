// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_user.cpp demonstrates how to create an IAM user for an AWS account.
*/
//snippet-start:[iam.cpp.create_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateUserRequest.h>
#include <aws/iam/model/CreateUserResult.h>
//snippet-end:[iam.cpp.create_user.inc]
//snippet-start:[iam.cpp.get_user.inc]
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>
//snippet-end:[iam.cpp.get_user.inc]
#include <iostream>

void CreateUser(const Aws::String& user_name)
{
    // snippet-start:[iam.cpp.create_user01.code]
    Aws::IAM::IAMClient iam;
    // snippet-end:[iam.cpp.create_user01.code]
    // snippet-start:[iam.cpp.get_user.code]
    Aws::IAM::Model::GetUserRequest get_request;
    get_request.SetUserName(user_name);

    auto get_outcome = iam.GetUser(get_request);
    if (get_outcome.IsSuccess())
    {
        std::cout << "IAM user " << user_name << " already exists" << std::endl;
        return;
    }
    else if (get_outcome.GetError().GetErrorType() !=
        Aws::IAM::IAMErrors::NO_SUCH_ENTITY)
    {
        std::cout << "Error checking existence of IAM user " << user_name << ":"
            << get_outcome.GetError().GetMessage() << std::endl;
        return;
    }
    // snippet-end:[iam.cpp.get_user.code]

    // snippet-start:[iam.cpp.create_user02.code]
    Aws::IAM::Model::CreateUserRequest create_request;
    create_request.SetUserName(user_name);

    auto create_outcome = iam.CreateUser(create_request);
    if (!create_outcome.IsSuccess())
    {
        std::cout << "Error creating IAM user " << user_name << ":" <<
            create_outcome.GetError().GetMessage() << std::endl;
        return;
    }
    std::cout << "Successfully created IAM user " << user_name << std::endl;
    // snippet-end:[iam.cpp.create_user02.code]
}

/**
 * Creates an iam user based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: create_user <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String user_name(argv[1]);

        CreateUser(user_name);
    }Aws::ShutdownAPI(options);
    return 0;
}

