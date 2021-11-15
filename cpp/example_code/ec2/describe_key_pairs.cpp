// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
describe_key_pairs.cpp demonstrates how to retrieve information about Amazon EC2 key pairs.

*/


//snippet-start:[ec2.cpp.describe_key_pairs.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DescribeKeyPairsRequest.h>
#include <aws/ec2/model/DescribeKeyPairsResponse.h>
#include <iomanip>
#include <iostream>
//snippet-end:[ec2.cpp.describe_key_pairs.inc]

/**
 * Describes all instance key pairs
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // snippet-start:[ec2.cpp.describe_key_pairs.code]
        Aws::EC2::EC2Client ec2;
        Aws::EC2::Model::DescribeKeyPairsRequest request;

        auto outcome = ec2.DescribeKeyPairs(request);
        if (outcome.IsSuccess())
        {
            std::cout << std::left <<
                std::setw(32) << "Name" <<
                std::setw(64) << "Fingerprint" << std::endl;

            const auto &key_pairs = outcome.GetResult().GetKeyPairs();
            for (const auto &key_pair : key_pairs)
            {
                std::cout << std::left <<
                    std::setw(32) << key_pair.GetKeyName() <<
                    std::setw(64) << key_pair.GetKeyFingerprint() << std::endl;
            }
        }
        else
        {
            std::cout << "Failed to describe key pairs:" <<
                outcome.GetError().GetMessage() << std::endl;
        }
        // snippet-end:[ec2.cpp.describe_key_pairs.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}

