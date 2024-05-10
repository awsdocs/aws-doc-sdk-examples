// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0 


#pragma once
#ifndef SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_SAMPLES_H
#define SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace SdkCustomization {
        //! Use a custom response stream when downloading an object from an Amazon Simple
        //! Storage Service (Amazon S3) bucket.
        /*!
          \param bucketName: The Amazon S3 bucket name.
          \param objectKey: The object key.
          \param filePath: File path for custom response stream.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool customResponseStream(const Aws::String &bucketName,
                                  const Aws::String &objectKey,
                                  const Aws::String &filePath,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace SES
} // namespace AwsDoc
#endif //SDK_CUSTOMIZATION_EXAMPLES_SDK_CUSTOMIZATION_SAMPLES_H
