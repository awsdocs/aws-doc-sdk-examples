// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
update_user.cpp demonstrates how to update the name of an IAM user.]
*/

//snippet-start:[iam.cpp.update_user.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/UpdateUserRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.update_user.inc]

/**
 * Updates an iam user's name based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 3)
    {
        std::cout << "Usage: update_user <old_user_name> <new_user_name>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String old_name(argv[1]);
        Aws::String new_name(argv[2]);

        // snippet-start:[iam.cpp.update_user.code]
        Aws::IAM::IAMClient iam;

        Aws::IAM::Model::UpdateUserRequest request;
        request.SetUserName(old_name);
        request.SetNewUserName(new_name);

        auto outcome = iam.UpdateUser(request);
        if (outcome.IsSuccess())
        {
            std::cout << "IAM user " << old_name <<
                " successfully updated with new user name " << new_name <<
                std::endl;
        }
        else
        {
            std::cout << "Error updating user name for IAM user " << old_name <<
                ":" << outcome.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[iam.cpp.update_user.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

