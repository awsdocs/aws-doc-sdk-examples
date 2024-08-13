// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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

// snippet-start:[ec2.cpp.start_instance.inc]

#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/StartInstancesRequest.h>
// snippet-end:[ec2.cpp.start_instance.inc]
// snippet-start:[ec2.cpp.stop_instance.inc]
#include <aws/ec2/model/StopInstancesRequest.h>
// snippet-end:[ec2.cpp.stop_instance.inc]
#include <iostream>
#include "ec2_samples.h"

// snippet-start:[cpp.example_code.ec2.StartInstances]
//! Start an Amazon Elastic Compute Cloud (Amazon EC2) instance.
/*!
  \param instanceID: An EC2 instance ID.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::startInstance(const Aws::String &instanceId,
                                const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.start_instance.code]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);

    Aws::EC2::Model::StartInstancesRequest startRequest;
    startRequest.AddInstanceIds(instanceId);
    startRequest.SetDryRun(true);

    Aws::EC2::Model::StartInstancesOutcome dryRunOutcome = ec2Client.StartInstances(startRequest);
    if (dryRunOutcome.IsSuccess()) {
        std::cerr
                << "Failed dry run to start instance. A dry run should trigger an error."
                << std::endl;
        return false;
    } else if (dryRunOutcome.GetError().GetErrorType() !=
               Aws::EC2::EC2Errors::DRY_RUN_OPERATION) {
        std::cout << "Failed dry run to start instance " << instanceId << ": "
                  << dryRunOutcome.GetError().GetMessage() << std::endl;
        return false;
    }

    startRequest.SetDryRun(false);
    Aws::EC2::Model::StartInstancesOutcome startInstancesOutcome = ec2Client.StartInstances(startRequest);

    if (!startInstancesOutcome.IsSuccess()) {
        std::cout << "Failed to start instance " << instanceId << ": " <<
                  startInstancesOutcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Successfully started instance " << instanceId <<
                  std::endl;
    }
    // snippet-end:[ec2.cpp.start_instance.code]

    return startInstancesOutcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ec2.StartInstances]

// snippet-start:[cpp.example_code.ec2.StopInstances]
//! Stop an EC2 instance.
/*!
  \param instanceID: An EC2 instance ID.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::EC2::stopInstance(const Aws::String &instanceId,
                               const Aws::Client::ClientConfiguration &clientConfiguration) {
    // snippet-start:[ec2.cpp.stop_instance.code]
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    Aws::EC2::Model::StopInstancesRequest request;
    request.AddInstanceIds(instanceId);
    request.SetDryRun(true);

    Aws::EC2::Model::StopInstancesOutcome dryRunOutcome = ec2Client.StopInstances(request);
    if (dryRunOutcome.IsSuccess()) {
        std::cerr
                << "Failed dry run to stop instance. A dry run should trigger an error."
                << std::endl;
        return false;
    } else if (dryRunOutcome.GetError().GetErrorType() !=
               Aws::EC2::EC2Errors::DRY_RUN_OPERATION) {
        std::cout << "Failed dry run to stop instance " << instanceId << ": "
                  << dryRunOutcome.GetError().GetMessage() << std::endl;
        return false;
    }

    request.SetDryRun(false);
    Aws::EC2::Model::StopInstancesOutcome outcome = ec2Client.StopInstances(request);
    if (!outcome.IsSuccess()) {
        std::cout << "Failed to stop instance " << instanceId << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    } else {
        std::cout << "Successfully stopped instance " << instanceId <<
                  std::endl;
    }
    // snippet-end:[ec2.cpp.stop_instance.code]

    return outcome.IsSuccess();
}

void PrintUsage() {
    std::cout << "Usage: run_start_stop_instance <instance_id> <start|stop>" <<
              std::endl;
}
// snippet-end:[cpp.example_code.ec2.StopInstances]

/*
 *
 *  main function
 *
 *  Usage: 'sage: run_start_stop_instance <instance_id> <start|stop>"'
 *
 *  Prerequisites: An EC2 instance to start or stop.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        PrintUsage();
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String instance_id = argv[1];

        bool start_instance;
        if (Aws::Utils::StringUtils::CaselessCompare(argv[2], "start")) {
            start_instance = true;
        }
        else if (Aws::Utils::StringUtils::CaselessCompare(argv[2], "stop")) {
            start_instance = false;
        }
        else {
            PrintUsage();
            return 1;
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        if (start_instance) {
            AwsDoc::EC2::startInstance(instance_id, clientConfig);
        }
        else {
            AwsDoc::EC2::stopInstance(instance_id, clientConfig);
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
