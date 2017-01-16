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
#include <aws/iam/model/DeleteAccessKeyRequest.h>

#include <iostream>

/**
 * Deletes an access key from an IAM user, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 3)
    {
        std::cout << "Usage: iam_delete_access_key <user_name> <access_key_id>" << std::endl;
        return 1;
    }

    Aws::String userName(argv[1]);
    Aws::String accessKeyId(argv[2]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    Aws::IAM::IAMClient iamClient;

    Aws::IAM::Model::DeleteAccessKeyRequest deleteAccessKeyRequest;
    deleteAccessKeyRequest.SetUserName(userName);
    deleteAccessKeyRequest.SetAccessKeyId(accessKeyId);

    auto deleteAccessKeyOutcome = iamClient.DeleteAccessKey(deleteAccessKeyRequest);
    if(!deleteAccessKeyOutcome.IsSuccess())
    {
        std::cout << "Error deleting access key " << accessKeyId << " from user " << userName << ": " << deleteAccessKeyOutcome.GetError().GetMessage() << std::endl;
    }
    else
    {
        std::cout << "Successfully deleted access key " << accessKeyId << " for IAM user " << userName << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}



