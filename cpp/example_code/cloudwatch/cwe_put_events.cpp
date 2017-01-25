/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>

#include <aws/events/CloudWatchEventsClient.h>
#include <aws/events/model/PutEventsRequest.h>
#include <aws/events/model/PutEventsResult.h>

#include <aws/core/utils/Outcome.h>

#include <iostream>

Aws::String MakeDetails(const Aws::String &key, const Aws::String& value)
{
    Aws::Utils::Json::JsonValue valueEntry;
    valueEntry.AsString(value);

    Aws::Utils::Json::JsonValue detailMap;
    detailMap.WithObject(key, valueEntry);

    return detailMap.WriteReadable();
}

/**
 * Posts a sample cloudwatch event, based on command line input
 */
int main(int argc, char** argv)
{
    if (argc != 4)
    {
        std::cout << "Usage: cwe_put_events <resource_arn> <sample_key> <sample_value>" << std::endl;
        return 1;
    }

    Aws::String resourceArn(argv[1]);
    Aws::String eventKey(argv[2]);
    Aws::String eventValue(argv[3]);

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    {
        Aws::CloudWatchEvents::CloudWatchEventsClient cwe_client;

        Aws::CloudWatchEvents::Model::PutEventsRequestEntry eventEntry;
        eventEntry.SetDetail(MakeDetails(eventKey, eventValue));
        eventEntry.SetDetailType("sampleSubmitted");
        eventEntry.AddResources(resourceArn);
        eventEntry.SetSource("aws-sdk-cpp-cloudwatch-example");

        Aws::CloudWatchEvents::Model::PutEventsRequest putEventsRequest;
        putEventsRequest.AddEntries(eventEntry);

        auto putEventsOutcome = cwe_client.PutEvents(putEventsRequest);
        if (!putEventsOutcome.IsSuccess())
        {
            std::cout << "Failed to post cloudwatch event: " << putEventsOutcome.GetError().GetMessage() << std::endl;
        }
        else
        {
            std::cout << "Successfully posted cloudwatch event" << std::endl;
        }
    }

    Aws::ShutdownAPI(options);

    return 0;
}



