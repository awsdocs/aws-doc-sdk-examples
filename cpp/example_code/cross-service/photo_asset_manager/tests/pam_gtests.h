// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef S3_EXAMPLES_S3_GTESTS_H
#define S3_EXAMPLES_S3_GTESTS_H

#include <aws/core/Aws.h>
#include <memory>
#include <gtest/gtest.h>
#include <aws/dynamodb/model/ScalarAttributeType.h>

namespace AwsDocTest {

    class MyStringBuffer : public std::stringbuf {
        int underflow() override;
    };

    class PAM_GTests : public testing::Test {
    protected:

        void SetUp() override;

        void TearDown() override;

        static void SetUpTestSuite();

        static void TearDownTestSuite();

        static Aws::String preconditionError();

        void AddCommandLineResponses(const std::vector <std::string> &responses);

        static Aws::String uuidName(const Aws::String &prefix);

        void setDatabaseName(const std::string &databaseName);

        void addLabelsToDelete(const std::vector <std::string> &labelsToDelete);

        void addObjectToDelete(const std::string &bucket, const std::string &key);

        static std::string getCachedBucketName();

        static std::string getCachedTableName();

        // s_clientConfig must be a pointer because the client config must be initialized
        // after InitAPI.
        static std::unique_ptr <Aws::Client::ClientConfiguration> s_clientConfig;

    private:

        static bool deleteLabelsInTable(const std::string &databaseName,
                                        const std::vector <std::string> &labels);

        static bool deleteObjectInBucket(const std::string &bucketName,
                                         const std::string &objectName);

        static bool DeleteBucket(const Aws::String &bucketName);

        static bool CreateBucket(const Aws::String &bucketName);

        static bool
        createTable(const Aws::String &tableName, const Aws::String &partitionKey,
                    Aws::DynamoDB::Model::ScalarAttributeType type);

        static bool deleteTable(const Aws::String &tableName);

        static bool waitTableActive(const Aws::String &tableName);


        static bool suppressStdOut();

        static Aws::SDKOptions s_options;

        std::stringbuf m_coutBuffer;  // Used to silence cout.
        std::streambuf *m_savedBuffer = nullptr;

        MyStringBuffer m_cinBuffer;
        std::streambuf *m_savedInBuffer = nullptr;

        std::string m_databaseName;

        std::vector <std::string> m_labelsToDelete;
        std::vector <std::pair<std::string, std::string>> m_ObjectsToDelete;
        static std::string s_cachedS3Bucket;

        static std::string s_cachedDynamoDBTable;

    }; // PAM_GTests
} // AwsDocTest

#endif //S3_EXAMPLES_S3_GTESTS_H
