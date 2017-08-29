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
#include <aws/iam/model/CreateUserRequest.h>
#include <aws/iam/model/CreateUserResult.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>
#include <iostream>

void CreateUser(const Aws::String& user_name)
{
    Aws::IAM::IAMClient iam;
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

