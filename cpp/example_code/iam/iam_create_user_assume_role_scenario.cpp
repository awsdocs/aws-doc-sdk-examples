/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/CreateUserRequest.h>
#include <aws/iam/model/GetUserRequest.h>
#include <aws/iam/model/CreateRoleRequest.h>
#include <aws/iam/model/CreatePolicyRequest.h>
#include <aws/iam/model/GetUserResult.h>
#include <aws/iam/model/DeleteRoleRequest.h>
#include <aws/iam/model/DeletePolicyRequest.h>
#include <aws/iam/model/DetachRolePolicyRequest.h>
#include <aws/iam/model/DeleteUserRequest.h>
#include <aws/iam/model/AttachRolePolicyRequest.h>
#include <aws/iam/model/GetRolePolicyRequest.h>
#include <aws/iam/model/ListAttachedRolePoliciesRequest.h>
#include <aws/iam/model/Role.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/AssumeRoleRequest.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/core/utils/Document.h>
#include <aws/core/auth/AWSCredentials.h>
#include <chrono>
#include <thread>

 // TODO: Access management client
namespace AwsDoc {
    namespace IAM {
        bool IAMCreateUserAssumeRoleScenario(const Aws::Client::ClientConfiguration &clientConfig,
                                        bool logProgress);
        static bool DeleteRole(const Aws::IAM::IAMClient &client,
                               const Aws::IAM::Model::Role &role,
                               bool logProgress);
        static bool DeleteUser(const Aws::IAM::IAMClient &client,
                               const Aws::IAM::Model::User &user,
                               bool logProgress);
        static bool DeletePolicy(const Aws::IAM::IAMClient &client,
                               const Aws::IAM::Model::Policy &policy,
                               bool logProgress);

        static bool DetachPolicyFromRole(const Aws::IAM::IAMClient &client,
                                         const Aws::IAM::Model::Policy &policy,
                                         const Aws::IAM::Model::Role &role,
                                         bool logProgress);

        static bool DeleteCreatedEntities(const Aws::IAM::IAMClient &client,
                                          const Aws::IAM::Model::Role &role,
                                          const Aws::IAM::Model::User &user,
                                          const Aws::IAM::Model::Policy &policy,
                                          bool logProgress);
    }
}

        //! Scenario to create, copy, and delete S3 buckets and objects.
        // "IAM access" permissions are needed to run this code.
        // "STS assume role" permissions are need to run this code (note, it may be necessary to
        //    create a custom policy.)
        /*!
          \sa IAMCreateUserAssumeRoleScenario
          \param clientConfig Aws client configuration.
          \param logProgress enables verbose logging.
        */

bool AwsDoc::IAM::IAMCreateUserAssumeRoleScenario(const Aws::Client::ClientConfiguration &clientConfig,
                                     bool logProgress) {

    Aws::IAM::IAMClient client(clientConfig);
    Aws::IAM::Model::User user;
    Aws::IAM::Model::Role role;
    Aws::IAM::Model::Policy policy;


    // Create user
    if (!user.UserNameHasBeenSet()) {
        Aws::IAM::Model::CreateUserRequest request;
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String userName = "iam-demo-user-" +
                               Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetUserName(userName);

        Aws::IAM::Model::CreateUserOutcome outcome = client.CreateUser(request);
        if (!outcome.IsSuccess()) {
            std::cout << "Error creating IAM user " << userName << ":" <<
                                                                        outcome.GetError().GetMessage() << std::endl;
            return false;
        }
        else if (logProgress) {
                std::cout << "Successfully created IAM user " << userName << std::endl;
            }

        user = outcome.GetResult().GetUser();
    }

    // Create a role.
    {
        // Get Iam user
        Aws::String iamUserArn;
        {
            Aws::IAM::Model::GetUserRequest request;
            Aws::IAM::Model::GetUserOutcome outcome = client.GetUser(request);
            if (!outcome.IsSuccess())
            {
                std::cerr << "Error getting Iam user. " <<
                          outcome.GetError().GetMessage() << std::endl;

                DeleteCreatedEntities(client, role, user, policy, logProgress);
                return false;
            }
            else if (logProgress) {
                std::cout << "Successfully retrieved Iam user " << outcome.GetResult().GetUser().GetUserName() << std::endl;
            }

            iamUserArn = outcome.GetResult().GetUser().GetArn();
        }

        Aws::IAM::Model::CreateRoleRequest request;

        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String roleName = "demo-role-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetRoleName(roleName);

        // Build policy document for role.
        Aws::Utils::Document jsonStatement;
        jsonStatement.WithString("Effect", "Allow");

        Aws::Utils::Document jsonPrincipal;
        jsonPrincipal.WithString("AWS", iamUserArn);
        jsonStatement.WithObject("Principal", jsonPrincipal);
        jsonStatement.WithString("Action", "sts:AssumeRole");
        jsonStatement.WithObject("Condition", Aws::Utils::Document());

        Aws::Utils::Document policyDocument;
        policyDocument.WithString("Version", "2012-10-17");

        Aws::Utils::Array<Aws::Utils::Document> statements(1);
        statements[0] = jsonStatement;
        policyDocument.WithArray("Statement", statements);

        if (logProgress) {
            std::cout << "Setting policy for role\n   " << policyDocument.View().WriteCompact() << std::endl;
        }

        // Set role policy document as JSON string.
        request.SetAssumeRolePolicyDocument(policyDocument.View().WriteCompact());

        Aws::IAM::Model::CreateRoleOutcome outcome = client.CreateRole(request);
        if (!outcome.IsSuccess())
        {
            std::cerr << "Error creating role. " <<
                      outcome.GetError().GetMessage() << std::endl;

            DeleteCreatedEntities(client, role, user,  policy, logProgress);
            return false;
        }
        else if (logProgress) {
            std::cout << "Successfully created a role with name " << roleName << std::endl;
        }

        role = outcome.GetResult().GetRole();
    }

    // Create an IAM policy
    {
        Aws::IAM::Model::CreatePolicyRequest request;
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String policyName = "demo-policy-" +
                               Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetPolicyName(policyName);

        // Build IAM policy document.
        Aws::Utils::Document jsonStatement;
        jsonStatement.WithString("Effect", "Allow");
        jsonStatement.WithString("Action", "s3:ListAllMyBuckets");
        jsonStatement.WithString("Resource", "arn:aws:s3:::*");

        Aws::Utils::Document policyDocument;
        policyDocument.WithString("Version", "2012-10-17");

        Aws::Utils::Array<Aws::Utils::Document> statements(1);
        statements[0] = jsonStatement;
        policyDocument.WithArray("Statement", statements);

        if (logProgress) {
            std::cout << "Creating a policy.\n   " << policyDocument.View().WriteCompact() << std::endl;
        }

        // Set IAM policy document as JSON string.
        request.SetPolicyDocument(policyDocument.View().WriteCompact());

        Aws::IAM::Model::CreatePolicyOutcome outcome = client.CreatePolicy(request);
        if (!outcome.IsSuccess())
        {
            std::cerr << "Error creating policy. " <<
                      outcome.GetError().GetMessage() << std::endl;

            DeleteCreatedEntities(client, role, user, policy, logProgress);
            return false;
        }
        else if (logProgress) {
            std::cout << "Successfully created a policy with name, " << policyName <<
            "." << std::endl;
        }

        policy = outcome.GetResult().GetPolicy();
    }

    Aws::STS::Model::Credentials credentials;
    // Assume the new role using the AWS Security Token Service (STS).
    {
        Aws::STS::STSClient stsClient(clientConfig);

        Aws::STS::Model::AssumeRoleRequest request;
        request.SetRoleArn(role.GetArn());
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String roleSessionName = "demo-role-session-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetRoleSessionName(roleSessionName);

        Aws::STS::Model::AssumeRoleOutcome assumeRoleOutcome;

        // The call to AssumeRole is repeated, because there is usually a delay
        // while the role is being made available to be assumed.
        // Repeat for a max of 20 times when access is denied.
        int count = 0;
        while (true) {
            assumeRoleOutcome = stsClient.AssumeRole(request);
            if (!assumeRoleOutcome.IsSuccess()) {
                if (count > 20 ||
                        assumeRoleOutcome.GetError().GetErrorType() != Aws::STS::STSErrors::ACCESS_DENIED) {
                    std::cerr << "Error assuming role after 20 tries. " <<
                              assumeRoleOutcome.GetError().GetMessage() << std::endl;

                    DeleteCreatedEntities(client, role, user, policy, logProgress);
                    return false;
                }
                std::this_thread::sleep_for(std::chrono::seconds(1));
           }
            else {
                if (logProgress) {
                    std::cout << "Successfully assumed the role after " << count << " seconds." << std::endl;
                }
                break;
            }
            count++;
        }

        // configure S3 client with assumed credentials
        credentials = assumeRoleOutcome.GetResult().GetCredentials();

    }

        // Attempt list buckets without the policy with S3 access priviledges assigned to role.
        // This should fail with ACCESS_DENIED
    {
        Aws::S3::S3Client s3Client(Aws::Auth::AWSCredentials(credentials.GetAccessKeyId(),
                                   credentials.GetSecretAccessKey(),
                                   credentials.GetSessionToken()),
                clientConfig);
        Aws::S3::Model::ListBucketsOutcome listBucketsOutcome = s3Client.ListBuckets();
        if (!listBucketsOutcome.IsSuccess())
        {
            if (listBucketsOutcome.GetError().GetErrorType() != Aws::S3::S3Errors::ACCESS_DENIED) {
                std::cerr << "Could not lists buckets. " <<
                          listBucketsOutcome.GetError().GetMessage() << std::endl;
            }
            else if (logProgress)
            {
                std::cout << "Access to list buckets denied because privileges have not been applied."
                            << std::endl;
            }
        }
        else {
            std::cerr << "Successfully retrieved bucket lists when this should not happen." << std::endl;
        }
    }

    // Attach the policy to the role.
    {
        Aws::IAM::Model::AttachRolePolicyRequest request;
        request.SetRoleName(role.GetRoleName());
        request.WithPolicyArn(policy.GetArn());

        Aws::IAM::Model::AttachRolePolicyOutcome outcome = client.AttachRolePolicy(request);
        if (!outcome.IsSuccess())
        {
            std::cerr << "Error creating policy. " <<
                      outcome.GetError().GetMessage() << std::endl;

            DeleteCreatedEntities(client, role, user, policy, logProgress);
            return false;
        }
        else if (logProgress) {
            std::cout << "Successfully attached the policy with name, " << policy.GetPolicyName() <<
                      ", to the role, " << role.GetRoleName() << "." << std::endl;
        }
    }

    int count = 0;
    // List buckets.
    // The call to ListBuckets is repeated, because there is usually a delay
    // while the policy is being applied to the role.
    // Repeat for a max of 20 times when access is denied.
    while (true)
    {
        Aws::S3::S3Client s3Client(Aws::Auth::AWSCredentials(credentials.GetAccessKeyId(),
                                                             credentials.GetSecretAccessKey(),
                                                             credentials.GetSessionToken()),
                                   clientConfig);
        Aws::S3::Model::ListBucketsOutcome listBucketsOutcome = s3Client.ListBuckets();
        if (!listBucketsOutcome.IsSuccess())
        {
            if ((count > 20) ||
            listBucketsOutcome.GetError().GetErrorType() != Aws::S3::S3Errors::ACCESS_DENIED) {
                std::cerr << "Could not lists buckets. " <<
                          listBucketsOutcome.GetError().GetMessage() << std::endl;
                break;
            }

            std::this_thread::sleep_for(std::chrono::milliseconds (1000));
        }
        else
        {
            if (logProgress) {
                std::cout << "Successfully retrieved bucket lists after " << count
                << " seconds." << std::endl;
            }
            break;
        }
        count++;
    }

    return DeleteCreatedEntities(client, role, user, policy, logProgress);
}

bool AwsDoc::IAM::DeleteCreatedEntities(const Aws::IAM::IAMClient &client,
                                  const Aws::IAM::Model::Role &role,
                                  const Aws::IAM::Model::User &user,
                                  const Aws::IAM::Model::Policy &policy,
                                  bool logProgress) {
    bool result = true;
    if (policy.ArnHasBeenSet())
    {
        result &= DetachPolicyFromRole(client, policy, role, logProgress);
        result &= DeletePolicy(client, policy, logProgress);
    }

    if (role.RoleIdHasBeenSet())
    {
        result &= DeleteRole(client, role, logProgress);
    }

    if (user.ArnHasBeenSet()) {
        result &= DeleteUser(client, user, logProgress);
    }

    return result;
}
bool AwsDoc::IAM::DeleteRole(const Aws::IAM::IAMClient &client, const Aws::IAM::Model::Role &role,
                             bool logProgress)
{
    Aws::IAM::Model::DeleteRoleRequest request;
    request.SetRoleName(role.GetRoleName());

    Aws::IAM::Model::DeleteRoleOutcome outcome = client.DeleteRole(request);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error deleting role. " <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    else if (logProgress) {
        std::cout << "Successfully deleted the role with name " << role.GetRoleName() << std::endl;
    }
    return true;
}

bool AwsDoc::IAM::DeleteUser(const Aws::IAM::IAMClient &client, const Aws::IAM::Model::User &user,
                             bool logProgress)
{
    Aws::IAM::Model::DeleteUserRequest request;
    request.WithUserName(user.GetUserName());

    Aws::IAM::Model::DeleteUserOutcome outcome = client.DeleteUser(request);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error deleting user. " <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    else if (logProgress) {
        std::cout << "Successfully deleted the user with name " << user.GetUserName() << std::endl;
    }
    return true;
}

bool AwsDoc::IAM::DeletePolicy(const Aws::IAM::IAMClient &client,
                               const Aws::IAM::Model::Policy &policy,
                         bool logProgress)
{
    Aws::IAM::Model::DeletePolicyRequest request;
    request.WithPolicyArn(policy.GetArn());

    Aws::IAM::Model::DeletePolicyOutcome outcome = client.DeletePolicy(request);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error deleting policy. " <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    else if (logProgress) {
        std::cout << "Successfully deleted the policy with arn " << policy.GetArn() << std::endl;
    }
    return true;
}

bool AwsDoc::IAM::DetachPolicyFromRole(const Aws::IAM::IAMClient &client,
                          const Aws::IAM::Model::Policy &policy,
                          const Aws::IAM::Model::Role &role,
                          bool logProgress)
{
    Aws::IAM::Model::DetachRolePolicyRequest request;
    request.SetPolicyArn(policy.GetArn());
    request.SetRoleName(role.GetRoleName());

    Aws::IAM::Model::DetachRolePolicyOutcome outcome = client.DetachRolePolicy(request);
    if (!outcome.IsSuccess())
    {
        std::cerr << "Error Detaching policy from roles. " <<
                  outcome.GetError().GetMessage() << std::endl;
        return false;
    }
    else if (logProgress) {
        std::cout << "Successfully detached the policy with arn " << policy.GetArn()
        << " from role " <<  role.GetRoleName() << "." << std::endl;
    }
    return true;
}

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        AwsDoc::IAM::IAMCreateUserAssumeRoleScenario(clientConfig, true);
    }

    ShutdownAPI(options);

    return 0;
}
