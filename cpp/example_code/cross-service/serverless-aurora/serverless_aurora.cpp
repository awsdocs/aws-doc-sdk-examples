/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ItemTrackerHTTPServer.h"
#include "RDSDataHandler.h"

int main(int argc, char** argv)
{
    AwsDoc::CrossService::RDSDataHandler rdsDataHandler;
    AwsDoc::CrossService::ItemTrackerHTTPServer itemTrackerHttpServer(rdsDataHandler);

    itemTrackerHttpServer.run(1, argv);

    return 0;
}