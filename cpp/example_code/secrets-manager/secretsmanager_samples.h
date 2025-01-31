// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef SECRETSMANAGER_EXAMPLES_SECRETSMANAGER_SAMPLES_H
#define SECRETSMANAGER_EXAMPLES_SECRETSMANAGER_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace SecretsManager {
        //! Retrieve an AWS Secrets Manager encrypted secret.
        /*!
          \param secretID: The ID for the secret.
          \return bool: Function succeeded.
         */
        bool getSecretValue(const Aws::String &secretID,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

    } // namespace SecretsManager
} // namespace AwsDoc
#endif //SECRETSMANAGER_EXAMPLES_SECRETSMANAGER_SAMPLES_H
