/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SES_EXAMPLES_SES_SAMPLES_H
#define SES_EXAMPLES_SES_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace SES {
        //! Create an Amazon Simple Email Service (Amazon SES) receipt filter.
        /*!
          \param receiptFilterName: The nane for the receipt filter.
          \param cidr: IP address or IP address range in Classless Inter-Domain Routing (CIDR) notation.
          \param policy: Block or allow enum of type ReceiptFilterPolicy.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createReceiptFilter(const Aws::String &receiptFilterName, Aws::String &cidr,
                         Aws::SES::Model::ReceiptFilterPolicy policy,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SES receipt rule.
        /*!
          \param receiptRuleName: The nane for the receipt rule.
          \param s3BucketName: The name of the S3 bucket for incoming mail.
          \param s3ObjectKeyPrefix: The prefix for the objects in the S3 bucket.
          \param ruleSetName: The name of the rule set where the receipt rule is added.
          \param recipients: Aws::Vector of recipients.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createReceiptRule(const Aws::String &receiptRuleName, Aws::String &s3BucketName,
                                 const Aws::String &s3ObjectKeyPrefix, const Aws::String &ruleSetName,
                                 const Aws::Vector<Aws::String> &recipients,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SES receipt filter.
        /*!
          \param receiptFilterName: The nane for the receipt Filter.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteReceiptFilter(const Aws::String &filterName,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace SES
} // namespace AwsDoc
#endif //SES_EXAMPLES_SES_SAMPLES_H
