// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#pragma once

#include <aws/core/Aws.h>
#include <awsdoc/acm/ACM_EXPORTS.h>

// Helper functions to demonstrate the various operations
// for AWS Certificate Manager.
namespace AwsDoc
{
    namespace ACM
    {
        AWSDOC_ACM_API bool AddTagToCertificate(const Aws::String& certificateArn,
            const Aws::String& tagKey, const Aws::String& tagValue,
            const Aws::String& region);
        AWSDOC_ACM_API bool DeleteCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool DescribeCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool ExportCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool GetCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool ImportCertificate(const Aws::String& certificateFile,
            const Aws::String& privateKeyFile,
            const Aws::String& certificateChainFile,
            const Aws::String& region,
            const Aws::String& certificateArn = "");
        AWSDOC_ACM_API bool ListCertificates(const Aws::String& region);
        AWSDOC_ACM_API bool ListTagsForCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool RemoveTagFromCertificate(const Aws::String& certificateArn,
            const Aws::String& tagKey,
            const Aws::String& region);
        AWSDOC_ACM_API bool RenewCertificate(const Aws::String& certificateArn,
            const Aws::String& region);
        AWSDOC_ACM_API bool RequestCertificate(const Aws::String& domainName,
            const Aws::String& idempotencyToken,
            const Aws::String& region);
        AWSDOC_ACM_API bool ResendValidationEmail(const Aws::String& certificateArn,
            const Aws::String& domainName,
            const Aws::String& validationDomain,
            const Aws::String& region);
        AWSDOC_ACM_API bool UpdateCertificateOption(const Aws::String& certificateArn,
            const Aws::String& region,
            const Aws::String& option);
    }
}
