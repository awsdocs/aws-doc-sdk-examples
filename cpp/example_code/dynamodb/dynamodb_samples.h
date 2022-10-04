/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H
#define DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace DynamoDB {
        bool dynamodbGettingStartedScenario(const Aws::Client::ClientConfiguration& clientConfiguration);
    } // DynamoDB
} // AwsDoc
#endif //DYNAMODB_EXAMPLES_DYANAMODB_SAMPLES_H
