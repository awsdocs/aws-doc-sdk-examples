/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <gtest/gtest.h>
#include "ItemTrackerHTTPHandler.h"
#include "RDSDataHandler.h"
#include "SES3EmailHandler.h"

static const Aws::String TABLE_NAME("items");

TEST(CrossService,serverless_aurora)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String database("auroraappdb");
        Aws::String resourceArn = std::getenv("RESOURCE_ARN");
        Aws::String secretArn = std::getenv("SECRET_ARN");
        Aws::String sesEmailAddress = std::getenv("EMAIL_ADDRESS");
        Aws::Client::ClientConfiguration clientConfig;

        AwsDoc::CrossService::RDSDataHandler rdsDataHandler(database, resourceArn,
                                                            secretArn, TABLE_NAME,
                                                            clientConfig);

        rdsDataHandler.initializeTable(true); // bool: recreate table.

        AwsDoc::CrossService::SES3EmailHandler sesEmailHandler(sesEmailAddress,
                                                               clientConfig);

        AwsDoc::CrossService::ItemTrackerHTTPHandler itemTrackerHttpServer(rdsDataHandler,
                                                                           sesEmailHandler);
    }

    ShutdownAPI(options);
}