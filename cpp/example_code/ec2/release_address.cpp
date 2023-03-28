/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

//snippet-start:[ec2.cpp.release_address.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/ReleaseAddressRequest.h>
#include <iostream>
//snippet-end:[ec2.cpp.release_address.inc]
#include "ec2_samples.h"

//! Release an Elastic IP address.
/*!
  \sa ReleaseAddress()
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::ReleaseAddress(const Aws::String &allocationID,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.release_address.code]
    Aws::EC2::EC2Client ec2(clientConfiguration);

    Aws::EC2::Model::ReleaseAddressRequest request;
    request.SetAllocationId(allocationID);

    auto outcome = ec2.ReleaseAddress(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to release Elastic IP address " <<
                  allocationID << ":" << outcome.GetError().GetMessage() <<
                  std::endl;
    }
    else {
        std::cout << "Successfully released Elastic IP address " <<
                  allocationID << std::endl;
    }
    // snippet-end:[ec2.cpp.release_address.code]

    return outcome.IsSuccess();
}

/*
 *
 *  main function
 *
 *  Usage: 'run_release_address <allocation_id>'
 *
 *  Prerequisites: An allocation ID for an Elastic IP address.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_release_address <allocation_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String allocationID = argv[1];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::EC2::ReleaseAddress(allocationID, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
