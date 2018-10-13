 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon GuardDuty]
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
#include <aws/guardduty/GuardDutyClient.h>
#include <aws/guardduty/model/ListDetectorsRequest.h>
#include <aws/guardduty/model/ListDetectorsResult.h>
#include <iostream>

/*
 * Lists GuardDuty detectors in the current AWS region.
 */

int main(int argc, char ** argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: list_detectors" << std::endl;

    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::GuardDuty::GuardDutyClient gd;

    Aws::GuardDuty::Model::ListDetectorsRequest ld_req;

    auto ld_out = gd.ListDetectors(ld_req);

    if (ld_out.IsSuccess())
    {
      std::cout << "Successfully listing the detectors:";

      for (auto val: ld_out.GetResult().GetDetectorIds())
      {
        std::cout << " " << val << std::endl;
      }
    }
    else
    {
      std::cout << "Error listing the detectors " << ld_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}

