/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SNS_EXAMPLES_SNS_SAMPLES_H
#define SNS_EXAMPLES_SNS_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace SNS {
        //! Create an SNS topic.
        /*!
          \sa createTopic()
          \param topicName: An SNS topic name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createTopic(const Aws::String &topicName,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an SNS topic.
        /*!
          \sa deleteTopic()
          \param topicARN: An SNS topic Amazon Resource Name (ARN).
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteTopic(const Aws::String &topicARN,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve the settings for sending Amazon SMS messages.
        /*!
          \sa getSMSType()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getSMSType(const Aws::Client::ClientConfiguration &clientConfiguration);
    } // DynamoDB
} // AwsDoc

#endif //SNS_EXAMPLES_SNS_SAMPLES_H
