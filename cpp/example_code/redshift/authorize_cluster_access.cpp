
//snippet-sourcedescription:[authorize_cluster_access.cpp demonstrates how to enable access to Amazon Redshift clusters. ]
//snippet-service:[redshift]
//snippet-keyword:[Amazon Redshift]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-02-05]
//snippet-sourceauthor:[AWS]


/*
Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/AuthorizeSecurityGroupIngressRequest.h>
#include <iostream>

/**
 * Enable access to Amazon Redshift clusters
 *
 * Defines a security group inbound rule for the default VPC. The rule
 * enables access to Redshift clusters by IP addresses referenced in the
 * ipAddress argument. To define the rule, EC2 permissions are required.
 */
bool AuthorizeClusterAccess(const Aws::String & ipAddress)
{
	Aws::EC2::EC2Client ec2;
	Aws::EC2::Model::AuthorizeSecurityGroupIngressRequest ec2_req;

	// Initialize request parameters
	ec2_req.SetGroupName("default");
	ec2_req.SetIpProtocol("tcp");
	ec2_req.SetFromPort(5439);		// Default Redshift port
	ec2_req.SetToPort(5439);		// Default Redshift port
	ec2_req.SetCidrIp(ipAddress);

	// Define the inbound rule
	auto outcome = ec2.AuthorizeSecurityGroupIngress(ec2_req);

	if (!outcome.IsSuccess())
	{
		std::cerr << "Error allowing cluster access. " << 
			outcome.GetError().GetMessage() << std::endl;
		return false;
	}
	return true;
}

/**
 * Exercise AuthorizeClusterAccess()
 */
int main(int argc, char **argv)
{
	Aws::SDKOptions options;
	Aws::InitAPI(options);
	{
		// Set these configuration values before running the program
		// The demo's ipAddress setting allows access from any computer. This 
		// is reasonable for demonstration purposes, but is not appropriate in
		// a production environment.
		Aws::String ipAddress = "0.0.0.0/0";

		if (!AuthorizeClusterAccess(ipAddress))
		{
			return 1;
		}

		std::cout << "Enabled access to Amazon Redshift clusters." << std::endl;
	}
	Aws::ShutdownAPI(options);
	return 0;
}
