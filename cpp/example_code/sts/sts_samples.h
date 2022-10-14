/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef README_MD_STS_SAMPLES_H
#define README_MD_STS_SAMPLES_H

#include <aws/core/Aws.h>

namespace AwsDoc {
    namespace STS {
        bool assumeRole(const Aws::String & roleArn,
                        const Aws::String & roleSessionName,
                        const Aws::String & externalId,
                        Aws::Auth::AWSCredentials & credentials,
                        const Aws::Client::ClientConfiguration &clientConfig);
    } // sts
}  // AwsDoc
#endif //README_MD_STS_SAMPLES_H
