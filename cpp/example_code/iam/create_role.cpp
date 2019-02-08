//snippet-sourcedescription:[create_role.cpp demonstrates how to create an Amazon IAM role.]
//snippet-service:[iam]
//snippet-keyword:[Amazon Identity and Access Management (IAM)]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-2-8]
//snippet-sourceauthor:[AWS]

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateRoleRequest.h>
#include <aws/iam/model/CreateRoleResult.h>
#include <aws/iam/model/Role.h>
#include <iostream>

/**
 * Create an IAM role
 */
Aws::IAM::Model::Role* CreateIamRole(
	const Aws::String& roleName,
	const Aws::String& policy,		// role trust policy
	Aws::IAM::Model::Role& role)
{
	Aws::IAM::IAMClient iam_client;
	Aws::IAM::Model::CreateRoleRequest iam_req;

	// Initialize the request
	iam_req.SetRoleName(roleName);
	iam_req.SetAssumeRolePolicyDocument(policy);

	// Create the role
	auto outcome = iam_client.CreateRole(iam_req);
	if (!outcome.IsSuccess())
	{
		std::cerr << "Error creating role. " << 
			outcome.GetError().GetMessage() << std::endl;
		return NULL;
	}

	// Return the created role
	auto result = outcome.GetResult();
	role = result.GetRole();
	return &role;
}

/**
 * Exercise CreateIamRole()
 */
int main()
{
	Aws::SDKOptions options;
	Aws::InitAPI(options);
	{
		// Set these configuration values before running the program
		Aws::String roleName = "RoleToAccessS3";
		
		// Define a role trust policy
		Aws::String roleTrustPolicy = R"({
			"Version": "2012-10-17",
			"Statement": {
				"Effect": "Allow",
				"Principal": {"Service": "ec2.amazonaws.com"},
				"Action": "sts:AssumeRole"
			}
		})";
		Aws::IAM::Model::Role iamRole;

		// Create the IAM role
		if (CreateIamRole(roleName, roleTrustPolicy, iamRole))
		{
			// Print some information about the role
			std::cout << "Created role " << iamRole.GetRoleName() << "\n";
			std::cout << "ID: " << iamRole.GetRoleId() << "\n";
			std::cout << "ARN: " << iamRole.GetArn() << std::endl;

			// After creating a role, define its permissions by either:
			//    -- Attaching a managed permissions policy by calling 
			//       AttachRolePolicy()
			//    -- Putting an inline permissions policy by calling 
			//       PutRolePolicy()
		}
	}
	Aws::ShutdownAPI(options);
	return 0;
}
