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
#include <aws/iam/model/DeleteUserRequest.h>
#include <aws/iam/model/Role.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <aws/core/utils/Document.h>

    //! Scenario to create, copy, and delete S3 buckets and objects.
    /*!
      \sa IAMCreateUserAssumeRoleScenario
      \param uploadFilePath path to file to upload to an S3 bucket.
      \param saveFilePath path for saving a downloaded S3 object.
      \param clientConfig Aws client configuration.
      \param logProgress enables verbose logging.
    */

static const char* const LIST_BUCKETS_POLICY =
    "{"
    "  \"Version\": \"2012-10-17\","
    "  \"Statement\": ["
    "    {"
    "        \"Effect\": \"Allow\","
    "        \"Action\": ["
    "            \"s3:ListAllMyBuckets\""
    "       ],"
    "        \"Resource\": \"arn:aws:s3:::*\""
    "    }"
    "   ]"
    "}";

namespace AwsDoc {
    namespace IAM {
        bool IAMCreateUserAssumeRoleScenario(const Aws::String &user_name,
                                        const Aws::Client::ClientConfiguration &clientConfig,
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
    }
}

bool AwsDoc::IAM::IAMCreateUserAssumeRoleScenario(const Aws::String &user_name,
                                     const Aws::Client::ClientConfiguration &clientConfig,
                                     bool logProgress) {

    Aws::IAM::IAMClient client(clientConfig);
    Aws::IAM::Model::User user;
    bool userWasCreated = false;

    // check if user exists
    {
        Aws::IAM::Model::GetUserRequest request;
        request.SetUserName(user_name);

        Aws::IAM::Model::GetUserOutcome outcome = client.GetUser(request);
        if (outcome.IsSuccess()) {
            if (logProgress) {
                std::cout << "IAM user " << user_name << " already exists" << std::endl;
            }
            user = outcome.GetResult().GetUser();
        }
        else if (outcome.GetError().GetErrorType() !=
                 Aws::IAM::IAMErrors::NO_SUCH_ENTITY) {
            std::cout << "Error checking existence of IAM user " << user_name << ":"
                      << outcome.GetError().GetMessage() << std::endl;
            return false;
        }
    }

    // if user does not exist, create user
    if (!user.UserNameHasBeenSet()) {
        Aws::IAM::Model::CreateUserRequest request;
        request.SetUserName(user_name);

        Aws::IAM::Model::CreateUserOutcome outcome = client.CreateUser(request);
        if (!outcome.IsSuccess()) {
            std::cout << "Error creating IAM user " << user_name << ":" <<
                                                                        outcome.GetError().GetMessage() << std::endl;
            return false;
        }
        else if (logProgress) {
                std::cout << "Successfully created IAM user " << user_name << std::endl;
            }

        user = outcome.GetResult().GetUser();
        userWasCreated = true;
    }

    // Create a role.
    Aws::IAM::Model::Role role;
    {
        Aws::IAM::Model::CreateRoleRequest request;

        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String roleName = "demo-role-" +
                                 Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetRoleName(roleName);

        // Build policy document for role.
        Aws::Utils::Document jsonStatement;
        jsonStatement.WithString("Effect", "Allow");

        Aws::Utils::Document jsonPrincipal;
        jsonPrincipal.WithString("AWS", user.GetArn());
        jsonStatement.WithObject("Principal", jsonPrincipal);
        jsonStatement.WithString("Action", "sts:AssumeRole");

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
            if (userWasCreated)
            {
                DeleteUser(client, user, logProgress);
            }
            return false;
        }
        else if (logProgress) {
            std::cout << "Successfully created a role with name " << roleName << std::endl;
        }

        role = outcome.GetResult().GetRole();
    }

    // Create an IAM policy
    Aws::IAM::Model::Policy policy;
    {
        Aws::IAM::Model::CreatePolicyRequest request;
        Aws::String uuid = Aws::Utils::UUID::RandomUUID();
        Aws::String policyName = "demo-policy-" +
                               Aws::Utils::StringUtils::ToLower(uuid.c_str());
        request.SetPolicyName(policyName);

        // Build IAM policy document.
        Aws::Utils::Document jsonStatement;
        jsonStatement.WithString("Effect", "Allow");
        jsonStatement.WithString("Action", "sts:AssumeRole");
        jsonStatement.WithString("Resource", "arn:aws:s3:::*");

        Aws::Utils::Document policyDocument;
        policyDocument.WithString("Version", "2012-10-17");

        Aws::Utils::Array<Aws::Utils::Document> statements(1);
        statements[0] = jsonStatement;
        policyDocument.WithArray("Statement", statements);

        if (logProgress) {
            std::cout << "Setting policy for role\n   " << policyDocument.View().WriteCompact() << std::endl;
        }

        // Set IAM policy document as JSON string.
        request.SetPolicyDocument(policyDocument.View().WriteCompact());

        Aws::IAM::Model::CreatePolicyOutcome outcome = client.CreatePolicy(request);
        if (!outcome.IsSuccess())
        {
            std::cerr << "Error creating policy. " <<
                      outcome.GetError().GetMessage() << std::endl;
            if (userWasCreated)
            {
                DeleteUser(client, user, logProgress);
            }
            DeleteRole(client, role, logProgress);
            return false;
        }
        else if (logProgress) {
            std::cout << "Successfully created a policy with name, " << policyName <<
            ", that lets the user, " << user_name << ", assume the role." << std::endl;
        }

        policy = outcome.GetResult().GetPolicy();
    }

    DeleteRole(client, role, logProgress);
    DeletePolicy(client, policy, logProgress);
    if (userWasCreated)
    {
        DeleteUser(client, user, logProgress);
    }

    return true;
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


int main(int argc, const char *argv[]) {

    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        AwsDoc::IAM::IAMCreateUserAssumeRoleScenario("test-user", clientConfig, true);
    }

    ShutdownAPI(options);

    return 0;
}
