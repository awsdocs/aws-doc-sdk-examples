/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "iam_gtests.h"
#include <aws/core/client/ClientConfiguration.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/model/AttachRolePolicyRequest.h>
#include <aws/iam/model/CreateAccessKeyRequest.h>
#include <aws/iam/model/CreateAccessKeyResult.h>
#include <aws/iam/model/CreateRoleRequest.h>
#include <aws/iam/model/CreateUserRequest.h>
#include <aws/iam/model/DeleteRoleRequest.h>
#include <aws/iam/model/DeleteAccessKeyRequest.h>
#include <aws/iam/model/DeletePolicyRequest.h>
#include <aws/iam/model/DeleteRolePolicyRequest.h>
#include <aws/iam/model/DeleteUserRequest.h>
#include <aws/iam/model/DeleteAccountAliasRequest.h>
#include <aws/iam/model/DetachRolePolicyRequest.h>
#include <aws/iam/model/CreatePolicyRequest.h>
#include <aws/iam/model/CreateAccountAliasRequest.h>
#include <aws/core/utils/UUID.h>

Aws::SDKOptions AwsDocTest::IAM_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::IAM_GTests::s_clientConfig;
Aws::String AwsDocTest::IAM_GTests::s_accessKey;
Aws::String AwsDocTest::IAM_GTests::s_role;
Aws::String AwsDocTest::IAM_GTests::s_userName;
Aws::String AwsDocTest::IAM_GTests::s_policyArn;

void AwsDocTest::IAM_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // "s_clientConfig" must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
    s_clientConfig->region = "us-east-1";
}

void AwsDocTest::IAM_GTests::TearDownTestSuite() {
    if (!s_policyArn.empty()) {
        deletePolicy(s_policyArn);
        s_policyArn.clear();
    }

    if (!s_role.empty()) {
        deleteRole(s_role);
        s_role.clear();
    }

    if (!s_accessKey.empty()) {
        deleteAccessKey(s_accessKey);
        s_accessKey.clear();
    }

    if (!s_userName.empty()) {
        deleteUser(s_userName);
        s_userName.clear();
    }

    ShutdownAPI(s_options);
}

void AwsDocTest::IAM_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);
}

void AwsDocTest::IAM_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }
}

Aws::String AwsDocTest::IAM_GTests::getExistingKey() {
    if (s_accessKey.empty()) {
        s_accessKey = createAccessKey();
    }

    return s_accessKey;
}

Aws::String AwsDocTest::IAM_GTests::getRole() {
    if (s_role.empty()) {
        s_role = createRole();
    }

    return s_role;
}

Aws::String AwsDocTest::IAM_GTests::createAccessKey() {
    auto userName = getUser();
    Aws::String result;

    if (!userName.empty()) {
        Aws::IAM::IAMClient iam(*s_clientConfig);

        Aws::IAM::Model::CreateAccessKeyRequest request;
        request.SetUserName(userName);

        auto outcome = iam.CreateAccessKey(request);
        if (!outcome.IsSuccess()) {
            std::cerr << "Error creating access key for IAM user " << userName
                      << ":" << outcome.GetError().GetMessage() << std::endl;
        }
        else {
            result = outcome.GetResult().GetAccessKey().GetAccessKeyId();
        }
    }

    return result;
}

Aws::String AwsDocTest::IAM_GTests::createRole() {
    Aws::IAM::IAMClient client(*s_clientConfig);
    Aws::IAM::Model::CreateRoleRequest request;

    Aws::String roleName = uuidName("role");

    request.SetRoleName(roleName);
    request.SetAssumeRolePolicyDocument(getAssumeRolePolicyJSON());

    Aws::IAM::Model::CreateRoleOutcome outcome = client.CreateRole(request);
    Aws::String result;
    if (!outcome.IsSuccess()) {
        std::cerr << "Error creating role. " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        result = roleName;
    }

    return result;
}

void AwsDocTest::IAM_GTests::deleteAccessKey(const Aws::String &accessKey) {

    Aws::IAM::IAMClient iam(*s_clientConfig);

    Aws::IAM::Model::DeleteAccessKeyRequest request;
    request.SetUserName(s_userName);
    request.SetAccessKeyId(accessKey);

    auto outcome = iam.DeleteAccessKey(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting access key " << accessKey << " from user "
                  << s_userName << ": " << outcome.GetError().GetMessage() <<
                  std::endl;
    }
}

void AwsDocTest::IAM_GTests::deleteRole(const Aws::String &role) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::DeleteRoleRequest request;
    request.SetRoleName(role);
    auto outcome = iam.DeleteRole(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleteRole " << outcome.GetError().GetMessage()
                  << std::endl;
    }
}

Aws::String AwsDocTest::IAM_GTests::getUser() {
    if (s_userName.empty()) {
        s_userName = createUser();
    }

    return s_userName;
}

Aws::String AwsDocTest::IAM_GTests::createUser() {
    Aws::String result;
    Aws::String userName = uuidName("user");

    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::CreateUserRequest create_request;
    create_request.SetUserName(userName);

    auto create_outcome = iam.CreateUser(create_request);
    if (!create_outcome.IsSuccess()) {
        std::cerr << "Error creating IAM user " << userName << ":" <<
                  create_outcome.GetError().GetMessage() << std::endl;
    }
    else {
        result = userName;
    }

    return result;
}

void AwsDocTest::IAM_GTests::deleteUser(const Aws::String &user) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::DeleteUserRequest request;
    request.SetUserName(user);
    auto outcome = iam.DeleteUser(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting IAM user " << user << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
}

Aws::String AwsDocTest::IAM_GTests::getAssumeRolePolicyJSON() {
    return R"({
            "Version": "2012-10-17",
            "Statement": {
                "Effect": "Allow",
                "Principal": {"Service": "ec2.amazonaws.com"},
                "Action": "sts:AssumeRole"
            }
        })";
}

Aws::String AwsDocTest::IAM_GTests::getRolePolicyJSON() {
    return R"({
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
}

Aws::String AwsDocTest::IAM_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

Aws::String AwsDocTest::IAM_GTests::createPolicy() {
    Aws::IAM::IAMClient iam(*s_clientConfig);

    Aws::IAM::Model::CreatePolicyRequest request;

    Aws::String policyName = uuidName("policy");
    request.SetPolicyName(policyName);
    request.SetPolicyDocument(R"({
            "Version": "2012-10-17",
            "Statement": {
                "Effect": "Allow",
                "Action": "logs:CreateLogGroup",
                "Resource": "arn:aws:logs:::*"
            }
        })");

    Aws::IAM::Model::CreatePolicyOutcome outcome = iam.CreatePolicy(request);
    Aws::String result;
    if (!outcome.IsSuccess()) {
        std::cerr << "Error creating policy " << policyName << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        result = outcome.GetResult().GetPolicy().GetArn();

    }
    return result;
}

Aws::String AwsDocTest::IAM_GTests::samplePolicyARN() {
    return "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess";
}

void AwsDocTest::IAM_GTests::deleteAccountAlias(const Aws::String &accountAlias) {
    Aws::IAM::IAMClient iam(*s_clientConfig);

    Aws::IAM::Model::DeleteAccountAliasRequest request;
    request.SetAccountAlias(accountAlias);

    const auto outcome = iam.DeleteAccountAlias(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting account alias " << accountAlias << ": "
                  << outcome.GetError().GetMessage() << std::endl;
    }
}

void AwsDocTest::IAM_GTests::detachRolePolicy(const Aws::String &role,
                                              const Aws::String &policyARN) {
    Aws::IAM::IAMClient iam(*s_clientConfig);

    Aws::IAM::Model::DetachRolePolicyRequest detachRequest;
    detachRequest.SetRoleName(role);
    detachRequest.SetPolicyArn(policyARN);

    auto detachOutcome = iam.DetachRolePolicy(detachRequest);
    if (!detachOutcome.IsSuccess()) {
        std::cerr << "Failed to detach policy " << policyARN << " from role "
                  << role << ": " << detachOutcome.GetError().GetMessage() <<
                  std::endl;
    }
}

Aws::String AwsDocTest::IAM_GTests::uuidName(const Aws::String &name) {
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    return "doc-example-tests-" + name + "-" +
           Aws::Utils::StringUtils::ToLower(uuid.c_str());
}

Aws::String AwsDocTest::IAM_GTests::getPolicy() {
    if (s_policyArn.empty()) {
        s_policyArn = createPolicy();
    }

    return s_policyArn;
}

void AwsDocTest::IAM_GTests::setUserName(const Aws::String &newName) {
    s_userName = newName;
}

Aws::String AwsDocTest::IAM_GTests::createAccountAlias() {
    Aws::String result;
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::CreateAccountAliasRequest request;
    auto aliasName = uuidName("alias");
    request.SetAccountAlias(aliasName);

    Aws::IAM::Model::CreateAccountAliasOutcome outcome = iam.CreateAccountAlias(
            request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error creating account alias " << aliasName << ": "
                  << outcome.GetError().GetMessage() << std::endl;
    }
    else {
        result = aliasName;
    }

    return result;
}

bool AwsDocTest::IAM_GTests::attachRolePolicy(const Aws::String &role,
                                              const Aws::String &policyArn) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::AttachRolePolicyRequest request;
    request.SetRoleName(role);
    request.SetPolicyArn(policyArn);

    auto outcome = iam.AttachRolePolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Failed to attach policy " << policyArn << " to role "
                  << role << ": " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

void AwsDocTest::IAM_GTests::deletePolicy(const Aws::String &policyArn) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::DeletePolicyRequest request;
    request.SetPolicyArn(policyArn);

    auto outcome = iam.DeletePolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleting policy with arn " << policyArn << ": "
                  << outcome.GetError().GetMessage() << std::endl;
    }

}

void AwsDocTest::IAM_GTests::deleteRolePolicy(const Aws::String &role,
                                              const Aws::String &policyName) {
    Aws::IAM::IAMClient iam(*s_clientConfig);
    Aws::IAM::Model::DeleteRolePolicyRequest request;
    request.SetRoleName(role);
    request.SetPolicyName(policyName);

    Aws::IAM::Model::DeleteRolePolicyOutcome outcome = iam.DeleteRolePolicy(request);
    if (!outcome.IsSuccess()) {
        std::cerr << "Error deleteRolePolicy " << outcome.GetError().GetMessage() <<
                  std::endl;
    }
}



