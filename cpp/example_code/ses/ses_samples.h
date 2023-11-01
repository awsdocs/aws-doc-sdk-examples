/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SES_EXAMPLES_SES_SAMPLES_H
#define SES_EXAMPLES_SES_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>
#include <aws/email/model/IdentityType.h>
#include <aws/email/model/ReceiptFilter.h>
#include <aws/email/model/ReceiptFilterPolicy.h>

namespace AwsDoc {
    namespace SES {
        //! Create an Amazon Simple Email Service (Amazon SES) receipt filter.
        /*!
          \param receiptFilterName: The name for the receipt filter.
          \param cidr: IP address or IP address range in Classless Inter-Domain Routing (CIDR) notation.
          \param policy: Block or allow enum of type ReceiptFilterPolicy.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createReceiptFilter(const Aws::String &receiptFilterName,
                                 const Aws::String &cidr,
                                 Aws::SES::Model::ReceiptFilterPolicy policy,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SES receipt rule.
        /*!
          \param receiptRuleName: The name for the receipt rule.
          \param s3BucketName: The name of the S3 bucket for incoming mail.
          \param s3ObjectKeyPrefix: The prefix for the objects in the S3 bucket.
          \param ruleSetName: The name of the rule set where the receipt rule is added.
          \param recipients: Aws::Vector of recipients.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createReceiptRule(const Aws::String &receiptRuleName,
                               const Aws::String &s3BucketName,
                               const Aws::String &s3ObjectKeyPrefix,
                               const Aws::String &ruleSetName,
                               const Aws::Vector<Aws::String> &recipients,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SES receipt rule set.
        /*!
          \param ruleSetName: The name of the rule set.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createReceiptRuleSet(const Aws::String &ruleSetName,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an Amazon SES template.
        /*!
          \param templateName: The name of the template.
          \param htmlPart: The HTML body of the email.
          \param subjectPart: The subject line of the email.
          \param textPart: The plain text version of the email.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createTemplate(const Aws::String &templateName,
                            const Aws::String &htmlPart,
                            const Aws::String &subjectPart,
                            const Aws::String &textPart,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete the specified identity (an email address or a domain).
        /*!
          \param identity: The identity to delete.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteIdentity(const Aws::String &identity,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SES receipt filter.
        /*!
          \param receiptFilterName: The nane for the receipt filter.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteReceiptFilter(const Aws::String &receiptFilterName,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SES receipt rule.
        /*!
          \param receiptRuleName: The nane for the receipt rule.
          \param receiptRuleSetName: The nane for the receipt rule set.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteReceiptRule(const Aws::String &receiptRuleName,
                               const Aws::String &receiptRuleSetName,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SES receipt rule set.
        /*!
          \param receiptRuleSetName: The nane for the receipt rule set.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteReceiptRuleSet(const Aws::String &receiptRuleSetName,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon SES template.
        /*!
          \param templateName: The nane for the template.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteTemplate(const Aws::String &templateName,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Get an Amazon SES template's attributes.
        /*!
          \param templateName: The name for the template.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getTemplate(const Aws::String &templateName,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the identities associated with this account.
        /*!
          \param identityType: The identity type enum. "NOT_SET" is a valid option.
          \param identities; A vector to receive the retrieved identities.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listIdentities(Aws::SES::Model::IdentityType identityType,
                            Aws::Vector<Aws::String> &identities,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the receipt filters associated with this account.
        /*!
          \param filters; A vector of "ReceiptFilter" to receive the retrieved filters.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listReceiptFilters(Aws::Vector<Aws::SES::Model::ReceiptFilter> &filters,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send an email to a list of recipients.
        /*!
          \param recipients; Vector of recipient email addresses.
          \param subject: Email subject.
          \param htmlBody: Email body as HTML. At least one body data is required.
          \param textBody: Email body as plain text. At least one body data is required.
          \param senderEmailAddress: Email address of sender. Ignored if empty string.
          \param ccAddresses: Vector of cc addresses. Ignored if empty.
          \param replyToAddress: Reply to email address. Ignored if empty string.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool sendEmail(const Aws::Vector<Aws::String> &recipients,
                       const Aws::String &subject,
                       const Aws::String &htmlBody,
                       const Aws::String &textBody,
                       const Aws::String &senderEmailAddress,
                       const Aws::Vector<Aws::String> &ccAddresses,
                       const Aws::String &replyToAddress,
                       const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Send a templated email to a list of recipients.
        /*!
          \param recipients; Vector of recipient email addresses.
          \param templateName: The name of the template to use.
          \param templateData: Map of key-value pairs for replacing text in template.
          \param senderEmailAddress: Email address of sender. Ignored if empty string.
          \param ccAddresses: Vector of cc addresses. Ignored if empty.
          \param replyToAddress: Reply to email address. Ignored if empty string.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool sendTemplatedEmail(const Aws::Vector<Aws::String> &recipients,
                                const Aws::String &templateName,
                                const Aws::Map<Aws::String, Aws::String> &templateData,
                                const Aws::String &senderEmailAddress,
                                const Aws::Vector<Aws::String> &ccAddresses,
                                const Aws::String &replyToAddress,
                                const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Update an Amazon SES template.
        /*!
          \param templateName: The name of the template.
          \param htmlPart: The HTML body of the email.
          \param subjectPart: The subject line of the email.
          \param textPart: The plain text version of the email.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateTemplate(const Aws::String &templateName,
                            const Aws::String &htmlPart,
                            const Aws::String &subjectPart,
                            const Aws::String &textPart,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Add an email address to the list of identities associated with this account and
        //! initiate verification.
        /*!
          \param emailAddress; The email address to add.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool verifyEmailIdentity(const Aws::String &emailAddress,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace SES
} // namespace AwsDoc
#endif //SES_EXAMPLES_SES_SAMPLES_H
