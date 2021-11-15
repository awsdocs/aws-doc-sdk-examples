// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
lookup_events.cpp demonstrates how to retrieve information about an AWS CloudTrail event.
*/
//snippet-start:[ct.cpp.lookup_events.inc]

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/LookupEventsRequest.h>
#include <aws/cloudtrail/model/LookupEventsResult.h>
#include <iostream>
//snippet-start:[ct.cpp.lookup_events.inc]

int main(int argc, char **argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: lookup_events";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    //snippet-start:[ct.cpp.lookup_events]
    Aws::CloudTrail::CloudTrailClient ct;

    Aws::CloudTrail::Model::LookupEventsRequest le_req;

    auto le_out = ct.LookupEvents(le_req);

    if (le_out.IsSuccess())
    {
      std::cout << "Successfully looking up cloudtrail events:";

      for (auto val: le_out.GetResult().GetEvents())
      {
        std::cout << " " << val.GetEventName() << std::endl;
      }
    }

    else
    {
      std::cout << "Error looking up cloudtrail events" << le_out.GetError().GetMessage()
        << std::endl;
    }
    //snippet-end:[ct.cpp.lookup_events]
  }

  Aws::ShutdownAPI(options);
  return 0;
}
//snippet-end:[ct.cpp.lookup_events]
