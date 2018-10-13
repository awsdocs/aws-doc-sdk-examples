//snippet-sourceauthor: [tapasweni-pathak]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

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
#include <aws/iam/model/CreateAccessKeyRequest.h>
#include <aws/iam/model/CreateAccessKeyResult.h>
#include <iostream>

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
    }
    Aws::ShutdownAPI(options);
    return 0;
}

