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

#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
// snippet-start:[ec2.cpp.configure_security_group.inc]
#include <aws/ec2/model/AuthorizeSecurityGroupIngressRequest.h>
// snippet-end:[ec2.cpp.configure_security_group.inc]
#include <iostream>
#include "ec2_samples.h"

static void buildSampleIngressRule(
        Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest &authorize_request);

// snippet-start:[cpp.example_code.ec2.AuthorizeSecurityGroupIngress]
//! Authorize ingress to an Amazon Elastic Compute Cloud (Amazon EC2) group.
/*!
  \param groupID: The EC2 group ID.
  \param clientConfiguration: The ClientConfiguration object.
  \return bool: True if the operation was successful, false otherwise.
 */
bool
AwsDoc::EC2::authorizeSecurityGroupIngress(const Aws::String &groupID,
                                           const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::EC2::EC2Client ec2Client(clientConfiguration);
    // snippet-start:[ec2.cpp.configure_security_group01.code]'
    Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest;
    authorizeSecurityGroupIngressRequest.SetGroupId(groupID);
    // snippet-end:[ec2.cpp.configure_security_group01.code]
    buildSampleIngressRule(authorizeSecurityGroupIngressRequest);

    // snippet-start:[ec2.cpp.configure_security_group03.code]
    Aws::EC2::Model::AuthorizeSecurityGroupIngressOutcome authorizeSecurityGroupIngressOutcome =
            ec2Client.AuthorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

    if (authorizeSecurityGroupIngressOutcome.IsSuccess()) {
        std::cout << "Successfully authorized security group ingress." << std::endl;
    } else {
        std::cerr << "Error authorizing security group ingress: "
                  << authorizeSecurityGroupIngressOutcome.GetError().GetMessage() << std::endl;
    }
    // snippet-end:[ec2.cpp.configure_security_group03.code]

    return authorizeSecurityGroupIngressOutcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ec2.AuthorizeSecurityGroupIngress]
// snippet-start:[cpp.example_code.ec2.BuildSampleIngressRule]
//! Build a sample ingress rule.
/*!
  \param authorize_request: An 'AuthorizeSecurityGroupIngressRequest' instance.
  \return void:
 */
void buildSampleIngressRule(
        Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest &authorize_request) {
    // snippet-start:[ec2.cpp.configure_security_group02.code]
    Aws::String ingressIPRange = "203.0.113.0/24";  // Configure this for your allowed IP range.
    Aws::EC2::Model::IpRange ip_range;
    ip_range.SetCidrIp(ingressIPRange);

    Aws::EC2::Model::IpPermission permission1;
    permission1.SetIpProtocol("tcp");
    permission1.SetToPort(80);
    permission1.SetFromPort(80);
    permission1.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission1);

    Aws::EC2::Model::IpPermission permission2;
    permission2.SetIpProtocol("tcp");
    permission2.SetToPort(22);
    permission2.SetFromPort(22);
    permission2.AddIpRanges(ip_range);

    authorize_request.AddIpPermissions(permission2);
    // snippet-end:[ec2.cpp.configure_security_group02.code]
}
// snippet-end:[cpp.example_code.ec2.BuildSampleIngressRule]

/*
 *
 *  main function
 *
 * Usage: run_authorize_security_group_ingress <group_id>
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: run_authorize_security_group_ingress <group_id>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String groupID = argv[1];
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the region where your resources reside.
        clientConfig.region = "us-east-1";

        AwsDoc::EC2::authorizeSecurityGroupIngress(groupID, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif //  TESTING_BUILD