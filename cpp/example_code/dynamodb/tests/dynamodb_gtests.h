/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <memory>
#include <vector>
#include <gtest/gtest.h>
#include <dynamodb/model/ScalarAttributeType.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class DynamoDB_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr<Aws::Client::ClientConfiguration> s_clientConfig;

        void AddCommandLineResponses(const std::vector<std::string> &responses);

        static Aws::String uuidName(const Aws::String &name);

        static bool createTableForScenario();

        static bool createSimpleTable();

        static bool
        createTable(const Aws::String &tableName, const Aws::String &partitionKey,
                    Aws::DynamoDB::Model::ScalarAttributeType type);

        static bool deleteTable(const Aws::String &tableName);

        static bool createBatchGetItemTables();

        static bool populateBatchTables();

        static bool deleteBatchGetItemTables();

        static bool putItem(const Aws::String &tableName,
                            const std::vector<Aws::String> &keys,
                            const std::vector<Aws::String> &values);

        static bool deleteItem(const Aws::String &tableName,
                               const std::vector<Aws::String> &keys,
                               const std::vector<Aws::String> &values);

        static bool s_batchTablesPopulated;
        static const Aws::String SIMPLE_TABLE_NAME;
        static const Aws::String SIMPLE_PRIMARY_KEY;

    private:
        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Use just to silence cout.
        std::streambuf *m_savedOutBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

        static bool s_ScenarioTableCreated;

        static bool s_SimpleTableCreated;

        static bool s_BatchTablesCreated;
    };
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
