/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once

#ifndef COGNITO_EXAMPLES_COGNITO_SAMPLES_H
#define COGNITO_EXAMPLES_COGNITO_SAMPLES_H

namespace AwsDoc {
    namespace Cognito {
        //! Scenario that adds a user to an Amazon Cognito user pool.
        /*!
          \sa gettingStartedWithUserPools()
          \param clientID: Client ID associated with an Amazon Cognito user pool.
          \param userPoolID: An Amazon Cognito user pool ID.
          \param clientConfig: Aws client configuration.
          \return bool: Successful completion.
         */
        bool gettingStartedWithUserPools(const Aws::String &clientID,
                                         const Aws::String &userPoolID,
                                         const Aws::Client::ClientConfiguration &clientConfig);
    } // namespace Cognito
} // namespace AwsDoc


#endif //COGNITO_EXAMPLES_COGNITO_SAMPLES_H
