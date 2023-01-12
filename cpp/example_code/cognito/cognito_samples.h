/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once

#ifndef COGNITO_EXAMPLES_COGNITO_SAMPLES_H
#define COGNITO_EXAMPLES_COGNITO_SAMPLES_H

namespace AwsDoc {
    namespace Cognito {
        bool gettingStartedWithUserPools(const Aws::String &clientID,
                                         const Aws::String &userPoolID,
                                         const Aws::Client::ClientConfiguration &clientConfig);
    } // namespace Cognito
} // namespace AwsDoc


#endif //COGNITO_EXAMPLES_COGNITO_SAMPLES_H
