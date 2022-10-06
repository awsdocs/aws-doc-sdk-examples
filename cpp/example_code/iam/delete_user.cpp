/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[iam.cpp.delete_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeleteUserRequest.h>
//snippet-end:[iam.cpp.delete_user.inc]
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/GetUserResult.h>
#include <iostream>

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
 * To delete a user in the non-trivial case, use the DeleteUser operation within the
 * aws-cpp-sdk-access-management high level sdk.
 *
 */

//! Creates an IAM user.
/*!
  \sa createUser()
  \param roleName: The user name.
  \param clientConfig: Aws client configuration.
  \return bool: Successful completion.
*/


void DeleteUser(const Aws::String& user_name)
{
    // snippet-start:[iam.cpp.delete_user01.code]
    Aws::IAM::IAMClient iam;
    // snippet-end:[iam.cpp.delete_user01.code]
    Aws::IAM::Model::GetUserRequest get_request;
    get_request.SetUserName(user_name);

    auto get_outcome = iam.GetUser(get_request);
    if (!get_outcome.IsSuccess())
    {
        if (get_outcome.GetError().GetErrorType() ==
            Aws::IAM::IAMErrors::NO_SUCH_ENTITY)
        {
            std::cout << "IAM user " << user_name << " does not exist" <<
                std::endl;
        }
        else
        {
            std::cout << "Error checking existence of IAM user " << user_name <<
                ": " << get_outcome.GetError().GetMessage() << std::endl;
        }
        return;
    }

    // snippet-start:[iam.cpp.delete_user02.code]
    Aws::IAM::Model::DeleteUserRequest request;
    request.SetUserName(user_name);
    auto outcome = iam.DeleteUser(request);
    if (!outcome.IsSuccess())
    {
        std::cout << "Error deleting IAM user " << user_name << ": " <<
            outcome.GetError().GetMessage() << std::endl;
        return;
    }
    std::cout << "Successfully deleted IAM user " << user_name << std::endl;
    // snippet-end:[iam.cpp.delete_user02.code]
}

/**
 * Deletes an IAM user based on command line input; only works for users with no
 * associated resources (groups, policies, etc...) To delete a user in the
 * non-trivial case, use the DeleteUser operation within the
 * aws-cpp-sdk-access-management high level sdk
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_user <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String user_name(argv[1]);

        DeleteUser(user_name);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

