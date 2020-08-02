// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#pragma once

#include <aws/core/Aws.h>
#include <awsdoc/s3-encryption/s3Encryption_EXPORTS.h>

namespace AwsDoc
{
    namespace S3Encryption
    {
        AWSDOC_S3ENCRYPTION_API bool KMSWithContextEncryptionMaterialsExample(
            const char* bucket,
            const char* objectKey,
            const char* masterKeyId
        );

        AWSDOC_S3ENCRYPTION_API bool SimpleEncryptionMaterialsWithGCMAADExample(
            const char* bucket,
            const char* objectKey
        );

        AWSDOC_S3ENCRYPTION_API bool DecryptObjectsEncryptedWithLegacyEncryptionExample(
            const char* bucket,
            const char* objectKey
        );

        AWSDOC_S3ENCRYPTION_API bool DecryptObjectsWithRangeExample(
            const char* bucket,
            const char* objectKey
        );

        AWSDOC_S3ENCRYPTION_API bool DecryptObjectsWithAnyCMKExample(
            const char* bucket,
            const char* objectKey,
            const char* masterKeyId
        );
    }
}
