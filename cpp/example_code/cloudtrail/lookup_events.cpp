 
//snippet-sourcedescription:[lookup_events.cpp demonstrates how to retrieve information about an AWS CloudTrail event.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CloudTrail]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/LookupEventsRequest.h>
#include <aws/cloudtrail/model/LookupEventsResult.h>
#include <iostream>

/**
 * Look up Cloud Trail events based on command line input
 */

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
  }

  Aws::ShutdownAPI(options);
  return 0;
}
