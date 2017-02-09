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
#include <aws/iam/model/CreateAccountAliasRequest.h>

#include <iostream>

/**
 * Creates an alias for an AWS account, based on command line input
 */
int main(int argc, char** argv)
{
    if(argc != 2)
    {
        std::cout << "Usage: iam_create_account_alias <alias_name>" << std::endl;
        return 1;
    }

    Aws::String aliasName(argv[1]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::IAM::IAMClient iamClient;

        Aws::IAM::Model::CreateAccountAliasRequest createAccountAliasRequest;
        createAccountAliasRequest.SetAccountAlias(aliasName);

        auto createAccountAliasOutcome = iamClient.CreateAccountAlias(createAccountAliasRequest);
        if (!createAccountAliasOutcome.IsSuccess())
        {
            std::cout << "Error creating account alias " << aliasName << ": " <<
            createAccountAliasOutcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully created account alias " << aliasName << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



