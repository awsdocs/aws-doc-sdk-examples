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

//snippet-start:[ec2.cpp.reboot_instance.inc]
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/RebootInstancesRequest.h>
#include <iostream>
//snippet-end:[ec2.cpp.reboot_instance.inc]
#include "ec2_samples.h"

//! Reboot an Amazon Elastic Compute Cloud (Amazon EC2) instance.
/*!
  \sa RebootInstance()
  \param instanceID: An EC2 instance ID.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::RebootInstance(const Aws::String &instanceId,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.reboot_instance.code]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);

    Aws::EC2::Model::RebootInstancesRequest request;
    request.AddInstanceIds(instanceId);
    request.SetDryRun(true);

    auto dry_run_outcome = ec2Client.RebootInstances(request);
    if (dry_run_outcome.IsSuccess()) {
        std::cerr
                << "Failed dry run to reboot on instance. A dry run should trigger an error."
                <<
                std::endl;
        return false;
    }
    else if (dry_run_outcome.GetError().GetErrorType()
             != Aws::EC2::EC2Errors::DRY_RUN_OPERATION) {
        std::cout << "Failed dry run to reboot instance " << instanceId << ": "
                  << dry_run_outcome.GetError().GetMessage() << std::endl;
        return false;
    }

    request.SetDryRun(false);
    auto outcome = ec2Client.RebootInstances(request);
    if (!outcome.IsSuccess()) {
        std::cout << "Failed to reboot instance " << instanceId << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully rebooted instance " << instanceId <<
                  std::endl;
    }
    // snippet-end:[ec2.cpp.reboot_instance.code]

    return outcome.IsSuccess();
}

/*
 *
 *  main function
 *
 *  Usage: 'sage: run_reboot_instance <instance_id>'
 *
 *  Prerequisites: An EC2 instance to reboot.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_reboot_instance <instance_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instanceId = argv[1];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::EC2::RebootInstance(instanceId, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD