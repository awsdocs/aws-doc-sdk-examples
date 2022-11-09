/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "serverless_aurora_gtests.h"
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

Aws::SDKOptions AwsDocTest::ServerlessAurora_GTests::s_options;
std::unique_ptr<Aws::Client::ClientConfiguration> AwsDocTest::ServerlessAurora_GTests::s_clientConfig;

void AwsDocTest::ServerlessAurora_GTests::SetUpTestSuite() {
    InitAPI(s_options);

    // "s_clientConfig" must be a pointer because the client config must be initialized
    // after InitAPI.
    s_clientConfig = std::make_unique<Aws::Client::ClientConfiguration>();
}

void AwsDocTest::ServerlessAurora_GTests::TearDownTestSuite() {

    ShutdownAPI(s_options);
}

void AwsDocTest::ServerlessAurora_GTests::SetUp() {
    m_savedBuffer = std::cout.rdbuf();
    std::cout.rdbuf(&m_coutBuffer);
}

void AwsDocTest::ServerlessAurora_GTests::TearDown() {
    if (m_savedBuffer != nullptr) {
        std::cout.rdbuf(m_savedBuffer);
        m_savedBuffer = nullptr;
    }
}


Aws::String AwsDocTest::ServerlessAurora_GTests::preconditionError() {
    return "Failed to meet precondition.";
}

Aws::Utils::Json::JsonValue AwsDocTest::ServerlessAurora_GTests::workItemToJson(
        const AwsDoc::CrossService::WorkItem &workItem) {
    Aws::Utils::Json::JsonValue jsonValue;
    jsonValue.WithString(AwsDoc::CrossService::HTTP_NAME_KEY, workItem.mName);
    jsonValue.WithString(AwsDoc::CrossService::HTTP_GUIDE_KEY, workItem.mGuide);
    jsonValue.WithString(AwsDoc::CrossService::HTTP_DESCRIPTION_KEY,
                         workItem.mDescription);
    jsonValue.WithString(AwsDoc::CrossService::HTTP_STATUS_KEY, workItem.mStatus);
    jsonValue.WithBool(AwsDoc::CrossService::HTTP_ARCHIVED_KEY, workItem.mArchived);
    return jsonValue;
}
