// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_key_pair.cpp demonstrates how to delete an Amazon EC2 key pair.

*/

//snippet-start:[ec2.cpp.delete_key_pair.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DeleteKeyPairRequest.h>
#include <iostream>
//snippet-end:[ec2.cpp.delete_key_pair.inc]

/**
 * Deletes an ec2 key pair based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 2)
    {
        std::cout << "Usage: delete_key_pair <key_pair_name>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String pair_name = argv[1];

        // snippet-start:[ec2.cpp.delete_key_pair.code]
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DeleteKeyPairRequest request;

        request.SetKeyName(pair_name);
        auto outcome = ec2.DeleteKeyPair(request);

        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to delete key pair " << pair_name <<
                ":" << outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully deleted key pair named " << pair_name <<
                std::endl;
        }
        // snippet-end:[ec2.cpp.delete_key_pair.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

