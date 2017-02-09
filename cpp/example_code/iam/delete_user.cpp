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
#include <aws/iam/model/DeleteUserRequest.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>

#include <iostream>

void DeleteUser(const Aws::String& userName)
{
    Aws::IAM::IAMClient iamClient;

    Aws::IAM::Model::GetUserRequest getUserRequest;
    getUserRequest.SetUserName(userName);

    auto getUserOutcome = iamClient.GetUser(getUserRequest);
    if (!getUserOutcome.IsSuccess())
    {
        if (getUserOutcome.GetError().GetErrorType() == Aws::IAM::IAMErrors::NO_SUCH_ENTITY)
        {
            std::cout << "IAM user " << userName << " does not exist" << std::endl;
        }
        else
        {
            std::cout << "Error checking existence of IAM user " << userName << ": " << getUserOutcome.GetError().GetMessage() << std::endl;
        }
        return;
    }

    Aws::IAM::Model::DeleteUserRequest deleteUserRequest;
    deleteUserRequest.SetUserName(userName);

    auto deleteUserOutcome = iamClient.DeleteUser(deleteUserRequest);
    if(!deleteUserOutcome.IsSuccess())
    {
        std::cout << "Error deleting IAM user " << userName << ": " << deleteUserOutcome.GetError().GetMessage() << std::endl;
        return;
    }

    std::cout << "Successfully deleted IAM user " << userName << std::endl;
}

/**
 * Deletes an IAM user based on command line input; only works for users with no associated resources (groups, policies, etc...)
 * To delete a user in the non-trivial case, use the DeleteUser operation within the aws-cpp-sdk-access-management high level sdk
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: iam_delete_user <user_name>" << std::endl;
        return 1;
    }

    Aws::String userName(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    DeleteUser(userName);

    Aws::ShutdownAPI(options);

    return 0;
}



