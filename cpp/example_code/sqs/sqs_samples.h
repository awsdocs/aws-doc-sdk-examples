/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SQS_EXAMPLES_SQS_SAMPLES_H
#define SQS_EXAMPLES_SQS_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>
#include <aws/sqs/model/QueueAttributeName.h>

namespace AwsDoc {
    namespace SQS {

        //! Changes the visibility timeout of a message in an Amazon Simple Queue Service
        //! (Amazon SQS) queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param messageReceiptHandle: A message receipt handle.
          \param visibilityTimeoutSeconds: Visibility timeout in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool changeMessageVisibility(
                const Aws::String &queue_url,
                const Aws::String &messageReceiptHandle,
                int visibilityTimeoutSeconds,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SQS queue.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createQueue(
                const Aws::String &queueName,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SQS queue configured for long polling.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param pollTimeSeconds: The receive message wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createLongPollingQueue(
                const Aws::String &queueName,
                const Aws::String &pollTimeSeconds,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a message from an Amazon SQS queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param messageReceiptHandle: A message receipt handle.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteMessage(const Aws::String &queueUrl,
                           const Aws::String &messageReceiptHandle,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SQS queue.
        /*!
          \param queueURL: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteQueue(const Aws::String &queueURL,
                         const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Get the URL for an Amazon SQS queue.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getQueueUrl(const Aws::String &queueName,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the Amazon SQS queues within an AWS account.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listQueues(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete the messages from an Amazon SQS queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool purgeQueue(const Aws::String &queueUrl,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Receive a message from an Amazon SQS queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool receiveMessage(const Aws::String &queueUrl,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Receive a message from an Amazon SQS queue specifying the wait time.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param waitTimeSeconds: The wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool receiveMessageWithWaitTime(const Aws::String &queueUrl,
                                        int waitTimeSeconds,
                                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send a message to an Amazon SQS queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param messageBody: A message body.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool sendMessage(const Aws::String &queueUrl,
                         const Aws::String &messageBody,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Connect an Amazon SQS queue to an associated dead-letter queue.
        /*!
          \param srcQueueUrl: An Amazon SQS queue URL.
          \param deadLetterQueueARN: The Amazon Resource Name (ARN) of a dead-letter queue.
          \param maxReceiveCount: The max receive count of a message before it is sent to the dead-letter queue.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool setDeadLetterQueue(const Aws::String &srcQueueUrl,
                                const Aws::String &deadLetterQueueARN,
                                int maxReceiveCount,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Set an Amazon SQS queue's poll time.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param pollTimeSeconds: The receive message wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool setQueueLongPollingAttribute(const Aws::String &queueURL,
                                          const Aws::String &pollTimeSeconds,
                                          const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Set the value of one of the Amazon SQS queue's attributes.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param attributeName: An attribute name enum.
          \param attribute: The attribute value as a string.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool setQueueAttributes(const Aws::String &queueURL,
                                Aws::SQS::Model::QueueAttributeName attributeName,
                                const Aws::String &attribute,
                                const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace SQS
} // namespace AwsDoc
#endif //SQS_EXAMPLES_SQS_SAMPLES_H
