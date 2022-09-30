/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once

#ifndef IAM_EXAMPLES_IAM_SAMPLES_H
#define IAM_EXAMPLES_IAM_SAMPLES_H

#include <aws/core/Aws.h>
#include <aws/iam/IAMClient.h>

namespace AwsDoc {
    namespace IAM {
        bool iamCreateUserAssumeRoleScenario(
                const Aws::Client::ClientConfiguration &clientConfig,
                bool logProgress);

        bool accessKeyLastUsed(const Aws::String &secretKeyID,
                               const Aws::Client::ClientConfiguration &clientConfig);

        bool attachRolePolicy(const Aws::String &roleName,
                         const Aws::String &policyArn,
                         const Aws::Client::ClientConfiguration &clientConfig);
        bool createAccessKey(const Aws::String &userName,
                             const Aws::Client::ClientConfiguration &clientConfig);

        bool createAccountAlias(const Aws::String& aliasName,
                                             const Aws::Client::ClientConfiguration &clientConfig);
    }
}

#endif //IAM_EXAMPLES_IAM_SAMPLES_H
