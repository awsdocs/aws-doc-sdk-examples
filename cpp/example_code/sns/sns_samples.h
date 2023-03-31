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
        //! Create an Amazon Simple Notification Service (Amazon SNS) topic.
        /*!
          \param topicName: An Amazon SNS topic name.
          \param topicARNResult: String to return the Amazon Resource Name (ARN) for the topic.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createTopic(const Aws::String &topicName, Aws::String &topicARNResult,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SNS topic.
        /*!
          \param topicARN: The ARN for an Amazon SNS topic.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteTopic(const Aws::String &topicARN,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve the default settings for sending SMS messages from your AWS account
        //! by using Amazon SNS.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getSMSType(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve the properties of an Amazon SNS topic.
        /*!
          \param topicARN: The ARN for an Amazon SNS topic.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getTopicAttributes(const Aws::String &topicARN,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve a list of Amazon SNS subscriptions.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        listSubscriptions(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve a list of Amazon SNS topics.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listTopics(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send an SMS text message to a phone number.
        /*!
          \param message: The message to publish.
          \param phoneNumber: The phone number of the recipient in E.164 format.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool publishSms(const Aws::String &message,
                        const Aws::String &phoneNumber,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send a message to an Amazon SNS topic.
        /*!
          \param message: The message to publish.
          \param topicARN: The ARN for an Amazon SNS topic.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool publishToTopic(const Aws::String &message,
                            const Aws::String &topicARN,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Set the default settings for sending SMS messages.
        /*!
          \param smsType: The type of SMS message that you will send by default.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool setSMSType(const Aws::String &smsType,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Subscribe to an Amazon SNS topic with delivery to a mobile app.
        /*!
          \param topicARN: The ARN for an Amazon SNS topic.
          \param endpointARN: The ARN for a mobile app or device endpoint.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool subscribeApp(const Aws::String &topicARN,
                          const Aws::String &endpointARN,
                          const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Subscribe to an Amazon SNS topic with delivery to an email address.
        /*!
          \param topicARN: The ARN for an Amazon SNS topic.
          \param emailAddress: An email address.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool subscribeEmail(const Aws::String &topicARN,
                            const Aws::String &emailAddress,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Subscribe to an Amazon SNS topic with delivery to an AWS Lambda function.
        /*!
          \param topicARN: The ARN for an Amazon SNS topic.
          \param lambdaFunctionARN: The ARN for an AWS Lambda function.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool subscribeLambda(const Aws::String &topicARN,
                             const Aws::String &lambdaFunctionARN,
                             const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a subscription to an Amazon SNS topic.
        /*!
          \param subscriptionARN: The ARN for an Amazon SNS topic subscription.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool unsubscribe(const Aws::String &topicSubscriptionARN,
                         const Aws::Client::ClientConfiguration &clientConfiguration);
    } // SNS
} // AwsDoc

#endif //SNS_EXAMPLES_SNS_SAMPLES_H
