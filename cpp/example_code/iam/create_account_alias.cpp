// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/*
Purpose:
create_account_alias.cpp demonstrates how to create an alias for an AWS account.]
*/
//snippet-start:[iam.cpp.create_account_alias.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateAccountAliasRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.create_account_alias.inc]

/**
 * Creates an alias for an AWS account, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: create_account_alias <alias_name>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alias_name(argv[1]);

        // snippet-start:[iam.cpp.create_account_alias.code]
        Aws::IAM::IAMClient iam;
        Aws::IAM::Model::CreateAccountAliasRequest request;
        request.SetAccountAlias(alias_name);

        auto outcome = iam.CreateAccountAlias(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error creating account alias " << alias_name << ": "
                << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully created account alias " << alias_name <<
                std::endl;
        }
        // snippet-end:[iam.cpp.create_account_alias.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

