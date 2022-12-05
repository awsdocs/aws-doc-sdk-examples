/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef LAMBDA_EXAMPLES_GTESTS_LAMBDA_SAMPLES_H
#define LAMBDA_EXAMPLES_GTESTS_LAMBDA_SAMPLES_H

namespace AwsDoc {
    namespace Lambda {
        extern Aws::String INCREMENT_RESUlT_PREFIX;
        extern Aws::String ARITHMETIC_RESUlT_PREFIX;

        //! Get started with functions scenario.
        /*!
         \\sa getStartedWithFunctionsScenario()
         \param clientConfig: AWS client configuration.
         \return bool: Successful completion.
         */
        bool getStartedWithFunctionsScenario(
                const Aws::Client::ClientConfiguration &clientConfig);
    } // Lambda
} // AwsDoc

#endif //LAMBDA_EXAMPLES_GTESTS_LAMBDA_SAMPLES_H
