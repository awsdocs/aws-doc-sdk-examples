/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "ItemTrackerHTTPHandler.h"
#include "RDSDataHandler.h"
#include "SES3EmailHandler.h"
#include "serverless_aurora_gtests.h"

namespace AwsDocTest {
    static const Aws::String TABLE_NAME("items");

    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(ServerlessAurora_GTests, test1) {
        Aws::String database("auroraappdb");
        const char* env_var = std::getenv("RESOURCE_ARN");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String resourceArn(env_var);
        env_var = std::getenv("SECRET_ARN");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String secretArn(env_var);
        env_var = std::getenv("EMAIL_ADDRESS");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String sesEmailAddress(env_var);
        env_var = std::getenv("DESTINATION_ADDRESS");
        ASSERT_NE(env_var, nullptr) << preconditionError();
        Aws::String destinationEmail(env_var);
        Aws::Client::ClientConfiguration clientConfig;

        ASSERT_FALSE(secretArn.empty()) << preconditionError();
        ASSERT_FALSE(sesEmailAddress.empty()) << preconditionError();
        ASSERT_FALSE(destinationEmail.empty()) << preconditionError();

        AwsDoc::CrossService::RDSDataHandler rdsDataHandler(database, resourceArn,
                                                            secretArn, TABLE_NAME,
                                                            clientConfig);

        rdsDataHandler.initializeTable(true); // bool: recreate table.

        AwsDoc::CrossService::SES3EmailHandler sesEmailHandler(sesEmailAddress,
                                                               clientConfig);

        AwsDoc::CrossService::ItemTrackerHTTPHandler itemTrackerHttpServer(
                rdsDataHandler,
                sesEmailHandler);
        std::string responseContentType;
        std::stringstream responseStream;

        // Test adding an item.
        AwsDoc::CrossService::WorkItem workItem1("", "Test 1", "dotnet",
                                                 "Automated Test 1", "In Progress 1",
                                                 false);

        Aws::String jsonString = workItemToJson(workItem1).View().WriteCompact();

        bool result = itemTrackerHttpServer.handleHTTP("POST", "/api/items",
                                                       jsonString,
                                                       responseContentType,
                                                       responseStream);

        ASSERT_TRUE(result);

        // Test retrieving all items equals the added item.
        responseContentType.clear();
        responseStream.str("");

        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        Aws::Utils::Json::JsonValue jsonValue = responseStream.str();
        auto jsonArray = jsonValue.View().AsArray();
        ASSERT_EQ(jsonArray.GetLength(), 1);

        auto id1 = jsonArray[0].GetString(AwsDoc::CrossService::HTTP_ID_KEY);
        ASSERT_EQ(workItem1.mName,
                  jsonArray[0].GetString(AwsDoc::CrossService::HTTP_NAME_KEY));
        ASSERT_EQ(workItem1.mGuide,
                  jsonArray[0].GetString(AwsDoc::CrossService::HTTP_GUIDE_KEY));
        ASSERT_EQ(workItem1.mDescription,
                  jsonArray[0].GetString(AwsDoc::CrossService::HTTP_DESCRIPTION_KEY));
        ASSERT_EQ(workItem1.mStatus,
                  jsonArray[0].GetString(AwsDoc::CrossService::HTTP_STATUS_KEY));
        ASSERT_EQ(workItem1.mArchived,
                  jsonArray[0].GetBool(AwsDoc::CrossService::HTTP_ARCHIVED_KEY));

        // Test adding another item.
        responseContentType.clear();
        responseStream.str("");

        AwsDoc::CrossService::WorkItem workItem2("", "Test 2", "cpp",
                                                 "Automated Test 2", "In Progress 2",
                                                 false);

        jsonString = workItemToJson(workItem1).View().WriteCompact();

        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("POST", "/api/items",
                                                  jsonString,
                                                  responseContentType, responseStream);

        ASSERT_TRUE(result);

        // Test retrieving all items equals 2 items.
        responseContentType.clear();
        responseStream.str("");

        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        ASSERT_EQ(jsonValue.View().AsArray().GetLength(), 2);

        // Test retrieving archived items equals 0 items.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items?archived=true", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        ASSERT_EQ(jsonValue.View().AsArray().GetLength(), 0);

        // Test retrieving not archived items equals 2 items.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items?archived=false",
                                                  "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        ASSERT_EQ(jsonValue.View().AsArray().GetLength(), 2);

        // Test setting one item as archived.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("PUT", Aws::String("/api/items/") +
                                                         id1 + ":archive", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);

        // Test retrieving all items equals 2 items.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        ASSERT_EQ(jsonValue.View().AsArray().GetLength(), 2);

        // Test retrieving archived items equals 1 item.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items?archived=true", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        jsonArray = jsonValue.View().AsArray();
        ASSERT_EQ(jsonArray.GetLength(), 1);

        ASSERT_EQ(id1, jsonArray[0].GetString(AwsDoc::CrossService::HTTP_ID_KEY));

        // Test retrieving not archived items equals 1 item.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items?archived=false",
                                                  "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        jsonArray = jsonValue.View().AsArray();
        ASSERT_EQ(jsonArray.GetLength(), 1);

        ASSERT_NE(id1, jsonArray[0].GetString(AwsDoc::CrossService::HTTP_ID_KEY));

        // Test changing the name of the archived item.
        responseContentType.clear();
        responseStream.str("");

        workItem1.mName = "changed name";
        workItem1.mArchived = true;
        jsonString = workItemToJson(workItem1).View().WriteCompact();
        result = itemTrackerHttpServer.handleHTTP("PUT", Aws::String("/api/items/") +
                                                         id1, jsonString,
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);

        // Test retrieving archived items with new name.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", "/api/items?archived=true", "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        jsonArray = jsonValue.View().AsArray();
        ASSERT_EQ(jsonArray.GetLength(), 1);

        ASSERT_EQ(workItem1.mName,
                  jsonArray[0].GetString(AwsDoc::CrossService::HTTP_NAME_KEY));

        // Test retrieving an item with id.
        responseContentType.clear();
        responseStream.str("");
        result = itemTrackerHttpServer.handleHTTP("GET", Aws::String("/api/items/") + id1, "",
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
        ASSERT_EQ(responseContentType, "application/json");
        jsonValue = responseStream.str();
        ASSERT_EQ(jsonValue.View().AsArray().GetLength(), 1);

        // Test sending email.
        responseContentType.clear();
        responseStream.str("");
        Aws::Utils::Json::JsonValue emailJson;
        emailJson.WithString(AwsDoc::CrossService::HTTP_EMAIL_KEY, destinationEmail);
        result = itemTrackerHttpServer.handleHTTP("POST", "/api/items:report",
                                                  emailJson.View().WriteCompact(),
                                                  responseContentType, responseStream);
        ASSERT_TRUE(result);
    }
} // namespace AwsDocTest