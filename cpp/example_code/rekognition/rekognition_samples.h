// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef EXAMPLES_REKOGNITION_SAMPLES_H
#define EXAMPLES_REKOGNITION_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace Rekognition {
        //! Detect instances of real-world entities within an image by using Amazon Rekognition
        /*!
          \param imageBucket: The Amazon Simple Storage Service (Amazon S3) bucket containing an image.
          \param imageKey: The Amazon S3 key of an image object.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool detectLabels(const Aws::String &imageBucket,
                          const Aws::String &imageKey,
                          const Aws::Client::ClientConfiguration &clientConfiguration);
    } // Rekognition
}// AwsDoc

#endif //EXAMPLES_REKOGNITION_SAMPLES_H
