// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
create_access_key.cpp demonstrates how to create a new AWS access key and AWS access key ID for an IAM user.
*/
//snippet-start:[iam.cpp.create_access_key.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateAccessKeyRequest.h>
#include <aws/iam/model/CreateAccessKeyResult.h>
#include <iostream>
//snippet-end:[iam.cpp.create_access_key.inc]

/**
 * Creates an access key for an iam user based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: create_access_key <user_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String user_name(argv[1]);

        // snippet-start:[iam.cpp.create_access_key.code]
        Aws::IAM::IAMClient iam;

        Aws::IAM::Model::CreateAccessKeyRequest request;
        request.SetUserName(user_name);

        auto outcome = iam.CreateAccessKey(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error creating access key for IAM user " << user_name
                << ":" << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            const auto &accessKey = outcome.GetResult().GetAccessKey();
            std::cout << "Successfully created access key for IAM user " <<
                user_name << std::endl << "  aws_access_key_id = " <<
                accessKey.GetAccessKeyId() << std::endl <<
                " aws_secret_access_key = " << accessKey.GetSecretAccessKey() <<
                std::endl;
        }
        // snippet-end:[iam.cpp.create_access_key.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

