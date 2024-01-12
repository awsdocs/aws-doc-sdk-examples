// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef TOPICS_AND_QUEUES_TOPICS_AND_QUEUES_SAMPLES_H
#define TOPICS_AND_QUEUES_TOPICS_AND_QUEUES_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace TopicsAndQueues {
        //! Workflow for messaging with topics and queues using Amazon Simple Notification
        //! Service (Amazon SNS) and Amazon Simple Queue Service (Amazon SQS).
        /*!
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        bool messagingWithTopicsAndQueues(const Aws::Client::ClientConfiguration &clientConfiguration);
    } // TopicsAndQueues
} // AwsDoc

#endif //TOPICS_AND_QUEUES_TOPICS_AND_QUEUES_SAMPLES_H
