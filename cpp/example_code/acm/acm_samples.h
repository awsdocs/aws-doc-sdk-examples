/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
        bool AddTagToCertificate(const Aws::String &certificateArn,
                                 const Aws::String &tagKey,
                                 const Aws::String &tagValue,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);


        //! Delete an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DeleteCertificate(const Aws::String &certificateArn,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DescribeCertificate(const Aws::String &certificateArn,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Export an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param passphrase: A passphrase to decrypt the exported certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool ExportCertificate(const Aws::String &certificateArn,
                               const Aws::String& passphrase,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Get an ACM certificate.
        /*!
          \param certificateArn: The Amazon Resource Name (ARN) of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool GetCertificate(const Aws::String &certificateArn,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        bool ImportCertificate(const Aws::String &certificateFile,
                               const Aws::String &privateKeyFile,
                               const Aws::String &certificateChainFile,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        bool ListCertificates(const Aws::String &region);

        bool ListTagsForCertificate(const Aws::String &certificateArn,
                                    const Aws::String &region);

        bool RemoveTagFromCertificate(const Aws::String &certificateArn,
                                      const Aws::String &tagKey,
                                      const Aws::String &region);

        bool RenewCertificate(const Aws::String &certificateArn,
                              const Aws::String &region);

        bool RequestCertificate(const Aws::String &domainName,
                                const Aws::String &idempotencyToken,
                                const Aws::String &region);

        bool ResendValidationEmail(const Aws::String &certificateArn,
                                   const Aws::String &domainName,
                                   const Aws::String &validationDomain,
                                   const Aws::String &region);

        bool UpdateCertificateOption(const Aws::String &certificateArn,
                                     const Aws::String &region,
                                     const Aws::String &option);
    }
}

#endif //ACM_EXAMPLES_ACM_SAMPLES_H
