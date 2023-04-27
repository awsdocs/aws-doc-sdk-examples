/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SQS_EXAMPLES_SQS_SAMPLES_H
#define SQS_EXAMPLES_SQS_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

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
        bool ChangeMessageVisibility(
                const Aws::String& queue_url,
                const Aws::String& messageReceiptHandle,
                int visibilityTimeoutSeconds,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool CreateQueue(
                const Aws::String& queueName,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon Simple Queue Service (Amazon SQS) queue configured for
        //! long polling.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param pollTimeSeconds: The receive message wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool CreateLongPollingQueue(
                const Aws::String& queueName,
                const Aws::String& pollTimeSeconds,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a message from an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param messageReceiptHandle: A message receipt handle.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DeleteMessage(const Aws::String& queueUrl,
                           const Aws::String& messageReceiptHandle,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueURL: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DeleteQueue(const Aws::String& queueURL,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Get the URL for an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueName: An Amazon SQS queue name.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool GetQueueURL(const Aws::String& queueName,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the Amazon Simple Queue Service (Amazon SQS) queues within an AWS account.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool ListQueues(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete the messages from an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool PurgeQueue(const Aws::String& queueUrl,
                                     const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Receive a message from an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool ReceiveMessage(const Aws::String &queueUrl,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Receive a message from an Amazon Simple Queue Service (Amazon SQS) queue
        //! specifying the wait time.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param waitTimeSeconds: The wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool ReceiveMessageWithWaitTime(const Aws::String &queueUrl,
                            int waitTimeSeconds,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send a message to an Amazon Simple Queue Service (Amazon SQS) queue.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param messageBody: A message body.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool SendMessage(const Aws::String& queueUrl,
                                      const Aws::String& messageBody,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Connect an Amazon Simple Queue Service (Amazon SQS) queue to an associated
        //! Amazon SQS dead letter queue.
        /*!
          \param srcQueueUrl: An Amazon SQS queue URL.
          \param deadLetterQueueARN: The Amazon Resource Name (ARN) of an Amazon SQS dead letter queue.
          \param maxReceiveCount: The max receive count of a message before it is sent to the dead letter queue.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool SetDeadLetterQueue(const Aws::String& srcQueueUrl,
                                const Aws::String& deadLetterQueueARN,
                                int maxReceiveCount,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Set an Amazon Simple Queue Service (Amazon SQS) queue's poll time.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param pollTimeSeconds: The receive message wait time in seconds.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool SetQueueLongPollingAttribute(const Aws::String& queueURL,
                                          const Aws::String& pollTimeSeconds,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Set the value of one of the Amazon Simple Queue Service (Amazon SQS) queue attributes.
        /*!
          \param queueUrl: An Amazon SQS queue URL.
          \param attributeName: An attribute name enum.
          \param attribute: The attribute value as a string.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool SetQueueAttributes(const Aws::String& queueURL,
                                             Aws::SQS::Model::QueueAttributeName attributeName,
                                             const Aws::String& attribute,
                                             const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace SQS
} // namespace AwsDoc
#endif //SQS_EXAMPLES_SQS_SAMPLES_H
