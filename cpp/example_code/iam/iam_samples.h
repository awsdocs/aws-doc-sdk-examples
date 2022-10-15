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
        bool IAMCreateUserAssumeRoleScenario(const Aws::Client::ClientConfiguration &clientConfig,
                                             bool logProgress);
    }
}

#endif //IAM_EXAMPLES_IAM_SAMPLES_H
