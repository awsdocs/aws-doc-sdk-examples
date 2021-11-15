// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_account_alias.cpp demonstrates how to delete an alias for an AWS account.
*/


//snippet-start:[iam.cpp.delete_account_alias.inc]
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/DeleteAccountAliasRequest.h>
#include <iostream>
//snippet-end:[iam.cpp.delete_account_alias.inc]

/**
 * Deletes an alias from an AWS account, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_account_alias <account_alias>" <<
            std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String alias_name(argv[1]);

        // snippet-start:[iam.cpp.delete_account_alias.code]
        Aws::IAM::IAMClient iam;

        Aws::IAM::Model::DeleteAccountAliasRequest request;
        request.SetAccountAlias(alias_name);

        const auto outcome = iam.DeleteAccountAlias(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Error deleting account alias " << alias_name << ": "
                << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted account alias " << alias_name <<
                std::endl;
        }
        // snippet-end:[iam.cpp.delete_account_alias.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

