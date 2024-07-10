// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0 

/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "s3_examples.h"
#include "S3_GTests.h"

namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_default_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "1",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                }
        );

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_MD5_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "2",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_CRC32_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "3",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_CRC32C_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "4",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_SHA1_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "5",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_SHA256_sdk_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "6",  // Choose from one of the following checksum algorithms
                                        "y",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_default_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "1",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_MD5_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "2",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_CRC32_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "3",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_CRC32C_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "4",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_SHA1_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "5",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }

// NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(S3_GTests, s3_object_integrity_workflow_SHA256_calc_checksum_2_
    ) {
        AddCommandLineResponses({
                                        "6",  // Choose from one of the following checksum algorithms
                                        "n",  // Let the SDK calculate the checksum for you? (y/n)
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "",   // Press Enter to continue...
                                        "y" // Would you like to delete the resources created in this workflow? (y/n)
                                });

        auto result = AwsDoc::S3::s3ObjectIntegrityWorkflow(*s_clientConfig);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest
