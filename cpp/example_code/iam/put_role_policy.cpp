//snippet-sourcedescription:[put_role_policy.cpp demonstrates how to put a role policy on an Amazon IAM role.]
//snippet-service:[iam]
//snippet-keyword:[Amazon Identity and Access Management (IAM)]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-2-8]
//snippet-sourceauthor:[AWS]

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/PutRolePolicyRequest.h>
#include <iostream>

/**
 * Put an inline permissions policy on an IAM role
 */
bool PutRolePolicy(
    const Aws::String& roleName,
    const Aws::String& policyName,
    const Aws::String& policyDocument)
{
    Aws::IAM::IAMClient iam_client;
    Aws::IAM::Model::PutRolePolicyRequest iam_req;

    // Initialize the request
    iam_req.SetRoleName(roleName);
    iam_req.SetPolicyName(policyName);
    iam_req.SetPolicyDocument(policyDocument);

    // Put the permissions policy on the role
    auto outcome = iam_client.PutRolePolicy(iam_req);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error putting policy on role. " << 
            outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    return true;
}

/**
 * Exercise PutRolePolicy()
 */
int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Set these configuration values before running the program
        Aws::String roleName = "RoleToAccessS3";	// An existing IAM role
        Aws::String policyName = "MyS3PermPolicy";

        // Define a permissions policy that enables S3 ReadOnly access
        Aws::String permissionsPolicy = R"({
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Action": [
                        "s3:Get*",
                        "s3:List*"
                    ],
                    "Resource": "*"
                }
            ]
        })";

        // Create the IAM role
        if (PutRolePolicy(roleName, policyName, permissionsPolicy))
        {
            std::cout << "Successfully put permissions policy on " << 
                roleName << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}
