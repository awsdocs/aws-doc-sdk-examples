// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef ACM_EXAMPLES_ACM_SAMPLES_H
#define ACM_EXAMPLES_ACM_SAMPLES_H

#include <aws/core/Aws.h>

namespace AwsDoc {
    namespace ACM {
        //! Add tags to an AWS Certificate Manager (ACM) certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param tagKey: The key for the tag.
          \param tagValue: The value for the tag.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool addTagsToCertificate(const Aws::String &certificateArn,
                                  const Aws::String &tagKey,
                                  const Aws::String &tagValue,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteCertificate(const Aws::String &certificateArn,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool describeCertificate(const Aws::String &certificateArn,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Export an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param passphrase: A passphrase to decrypt the exported certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool exportCertificate(const Aws::String &certificateArn,
                               const Aws::String &passphrase,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Get an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getCertificate(const Aws::String &certificateArn,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Import an ACM certificate.
        /*!
          \param certificateFile: Path to certificate to import.
          \param privateKeyFile: Path to file containing a private key.
          \param certificateChainFile: Path to file containing a PEM encoded certificate chain.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool importCertificate(const Aws::String &certificateFile,
                               const Aws::String &privateKeyFile,
                               const Aws::String &certificateChainFile,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the ACM certificates in an account.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        listCertificates(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List the tags for an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listTagsForCertificate(const Aws::String &certificateArn,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param tagKey: The key for the tag.
          \param tagValue: The value for the tag.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool removeTagsFromCertificate(const Aws::String &certificateArn,
                                       const Aws::String &tagKey,
                                       const Aws::String &tagValue,
                                       const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Renew an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool renewCertificate(const Aws::String &certificateArn,
                              const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Request an ACM certificate.
        /*!
          \param domainName: A fully qualified domain name.
          \param idempotencyToken: Customer chosen string for idempotency.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool requestCertificate(const Aws::String &domainName,
                                const Aws::String &idempotencyToken,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Resend the email that requests domain ownership validation.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param domainName: A fully qualified domain name.
          \param validationDomain: The base validation domain that will act as the suffix
                                    of the email addresses.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool resendValidationEmail(const Aws::String &certificateArn,
                                   const Aws::String &domainName,
                                   const Aws::String &validationDomain,
                                   const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Update an ACM certificate option.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param loggingEnabled: Boolean specifying logging enabled.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateCertificateOption(const Aws::String &certificateArn,
                                     bool loggingEnabled,
                                     const Aws::Client::ClientConfiguration &clientConfiguration);
    }
}

#endif //ACM_EXAMPLES_ACM_SAMPLES_H
