/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX - License - Identifier: Apache - 2.0*/

/*
Purpose:
describe_trails.cpp demonstrates how to retrieve information about an AWS CloudTrail resource.
*/

//snippet-start:[ct.cpp.describe_trials.inc]

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/DescribeTrailsRequest.h>
#include <aws/cloudtrail/model/DescribeTrailsResult.h>
#include <iostream>
//snippet-end:[ct.cpp.describe_trials.inc]

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
    //snippet-start:[ct.cpp.describe_trials]
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
    //snippet-end:[ct.cpp.describe_trials]
  }

  Aws::ShutdownAPI(options);
  return 0;
}

