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
#include <aws/iam/model/UpdateUserRequest.h>

#include <iostream>

/**
 * Updates an iam user's name based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: iam_update_user <old_user_name> <new_user_name>" << std::endl;
        return 1;
    }

    Aws::String oldUserName(argv[1]);
    Aws::String newUserName(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iamClient;

        Aws::IAM::Model::UpdateUserRequest updateUserRequest;
        updateUserRequest.SetUserName(oldUserName);
        updateUserRequest.SetNewUserName(newUserName);

        auto updateUserOutcome = iamClient.UpdateUser(updateUserRequest);
        if (updateUserOutcome.IsSuccess())
        {
            std::cout << "IAM user " << oldUserName << " successfully updated with new user name " << newUserName <<
            std::endl;
        }
        else
        {
            std::cout << "Error updating user name for IAM user " << oldUserName << ":" <<
            updateUserOutcome.GetError().GetMessage() << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}

