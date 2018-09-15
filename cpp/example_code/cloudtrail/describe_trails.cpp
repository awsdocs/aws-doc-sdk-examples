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
#include <aws/cloudtrail/model/DescribeTrailsRequest.h>
#include <aws/cloudtrail/model/DescribeTrailsResult.h>
#include <iostream>

/**
 * Describes all cloud trails in a AWS region on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: describe_trail";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {

    Aws::CloudTrail::CloudTrailClient ct;

    Aws::CloudTrail::Model::DescribeTrailsRequest dt_req;

    auto dt_out = ct.DescribeTrails(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully describing cloud trails:";

      for (auto val: dt_out.GetResult().GetTrailList())
      {
        std::cout << val.GetName() << std::endl;
      }
    }

    else
    {
      std::cout << "Error describing cloud trails" << dt_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
