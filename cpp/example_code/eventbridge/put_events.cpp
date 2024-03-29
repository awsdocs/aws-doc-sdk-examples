// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: put_events.cpp demonstrates how to post an Amazon CloudWatch event.
 *
 * Inputs:
 * - resource_arn: The Amazon Resource Name (ARN) of the event.
 * - event_key: The event key.
 * - event_value: The event value.
 *
 * Outputs:
 * The event is posted.
   * ///////////////////////////////////////////////////////////////////////// */
// snippet-start:[cw.cpp.put_events.inc]
#include <aws/core/Aws.h>
#include <aws/events/EventBridgeClient.h>
#include <aws/events/model/PutEventsRequest.h>
#include <aws/events/model/PutEventsResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>
// snippet-end:[cw.cpp.put_events.inc]

Aws::String MakeDetails(const Aws::String &key, const Aws::String& value)
{
    Aws::Utils::Json::JsonValue value_entry;
    value_entry.AsString(value);

    Aws::Utils::Json::JsonValue detail_map;
    detail_map.WithObject(key, value_entry);

    return detail_map.View().WriteReadable();
}

/**
 * Posts a sample cloudwatch event, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage:" << std::endl << "  put_events " <<
            "<resource_arn> <sample_key> <sample_value>" << std::endl;
        return 1;
    }

    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::String resource_arn(argv[1]);
        Aws::String event_key(argv[2]);
        Aws::String event_value(argv[3]);

        // snippet-start:[cw.cpp.put_events.code]
        Aws::CloudWatchEvents::EventBridgeClient cwe;

        Aws::CloudWatchEvents::Model::PutEventsRequestEntry event_entry;
        event_entry.SetDetail(MakeDetails(event_key, event_value));
        event_entry.SetDetailType("sampleSubmitted");
        event_entry.AddResources(resource_arn);
        event_entry.SetSource("aws-sdk-cpp-cloudwatch-example");

        Aws::CloudWatchEvents::Model::PutEventsRequest request;
        request.AddEntries(event_entry);

        auto outcome = cwe.PutEvents(request);
        if (!outcome.IsSuccess())
        {
            std::cout << "Failed to post CloudWatch event: " <<
                outcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully posted CloudWatch event" << std::endl;
        }
        // snippet-end:[cw.cpp.put_events.code]
    }
    Aws::ShutdownAPI(options);
    return 0;
}



